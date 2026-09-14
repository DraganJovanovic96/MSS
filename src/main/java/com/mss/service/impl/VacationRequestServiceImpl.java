package com.mss.service.impl;

import com.mss.dto.VacationRequestCreateDto;
import com.mss.dto.VacationRequestDto;
import com.mss.dto.VacationRequestUpdateDto;
import com.mss.enumeration.Role;
import com.mss.enumeration.VacationRequestStatus;
import com.mss.mapper.UserMapper;
import com.mss.mapper.VacationRequestMapper;
import com.mss.model.User;
import com.mss.model.VacationRequest;
import com.mss.repository.VacationRequestRepository;
import com.mss.service.UserService;
import com.mss.service.VacationRequestService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * Implementation of the VacationRequestService interface.
 * <p>
 * Provides methods to manage vacation request operations.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class VacationRequestServiceImpl implements VacationRequestService {

    /**
     * The repository used to retrieve vacation request data.
     */
    private final VacationRequestRepository vacationRequestRepository;

    /**
     * The repository used to retrieve user data.
     */
    private final com.mss.repository.UserRepository userRepository;

    /**
     * The service used to retrieve user data.
     */
    private final UserService userService;

    /**
     * The mapper used to map vacation request data.
     */
    private final VacationRequestMapper vacationRequestMapper;

    /**
     * The mapper used to map user data.
     */
    private final UserMapper userMapper;

    /**
     * Created VACATION_REQUEST_FILTER attribute, so we can change Filter easily if needed.
     */
    private static final String VACATION_REQUEST_FILTER = "deletedVacationRequestFilter";

    /**
     * An EntityManager instance is associated with a persistence context.
     * A persistence context is a set of entity instances in which for any
     * persistent entity identity there is a unique entity instance.
     */
    private final EntityManager entityManager;

    /**
     * The base number of vacation days per year.
     */
    @Value("${BASE_VACATION_DAYS}")
    private Integer baseVacationDays;
    /**
     * The deadline for using vacation days from the previous year (June 30).
     */
    private static final int VACATION_DEADLINE_MONTH = 6;
    private static final int VACATION_DEADLINE_DAY = 30;

    /**
     * Create a new vacation request for the authenticated user.
     *
     * @param vacationRequestCreateDto the vacation request data
     * @return the created vacation request
     */
    @Override
    @Transactional
    public VacationRequestDto createVacationRequest(VacationRequestCreateDto vacationRequestCreateDto) {
        User employee = userService.getUserFromAuthentication();

        if (vacationRequestCreateDto.getStartDate().isAfter(vacationRequestCreateDto.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before or equal to end date");
        }

        Integer businessDays = calculateBusinessDays(vacationRequestCreateDto.getStartDate(), vacationRequestCreateDto.getEndDate());
        if (businessDays <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vacation period must include at least one business day");
        }

        Integer vacationYear = vacationRequestCreateDto.getStartDate().getYear();
        if (vacationRequestCreateDto.getStartDate().getMonthValue() >= 7) {
            vacationYear = vacationRequestCreateDto.getStartDate().getYear() + 1;
        }

        Integer remainingDays = getRemainingVacationDaysForUser(employee.getId(), vacationYear);
        if (businessDays > remainingDays) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                String.format("Not enough vacation days. Requested: %d, Available: %d", businessDays, remainingDays));
        }

        if (vacationRequestRepository.hasOverlappingVacationRequests(
                employee.getId(), 
                vacationRequestCreateDto.getStartDate(), 
                vacationRequestCreateDto.getEndDate(), 
                null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vacation request overlaps with an existing request");
        }

        VacationRequest vacationRequest = VacationRequest.builder()
                .employee(employee)
                .startDate(vacationRequestCreateDto.getStartDate())
                .endDate(vacationRequestCreateDto.getEndDate())
                .daysUsed(businessDays)
                .status(VacationRequestStatus.PENDING)
                .vacationYear(vacationYear)
                .build();

        VacationRequest savedRequest = vacationRequestRepository.save(vacationRequest);
        return vacationRequestMapper.vacationRequestToVacationRequestDto(savedRequest);
    }

    /**
     * Get all vacation requests for the authenticated user.
     *
     * @return list of vacation requests for the authenticated user
     */
    @Override
    public List<VacationRequestDto> getMyVacationRequests() {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(VACATION_REQUEST_FILTER);
        filter.setParameter("isDeleted", false);

        User employee = userService.getUserFromAuthentication();
        List<VacationRequest> requests = vacationRequestRepository.findByEmployeeIdOrderByStartDateAsc(employee.getId());

        session.disableFilter(VACATION_REQUEST_FILTER);

        return vacationRequestMapper.vacationRequestsToVacationRequestDtos(requests);
    }

    /**
     * Get all vacation requests (admin only).
     *
     * @return list of all vacation requests
     */
    @Override
    public List<VacationRequestDto> getAllVacationRequests() {
        User currentUser = userService.getUserFromAuthentication();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view all vacation requests");
        }

        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(VACATION_REQUEST_FILTER);
        filter.setParameter("isDeleted", false);

        List<VacationRequest> requests = vacationRequestRepository.findAllByOrderByStartDateAsc();

        session.disableFilter(VACATION_REQUEST_FILTER);

        return vacationRequestMapper.vacationRequestsToVacationRequestDtos(requests);
    }

    /**
     * Get all pending vacation requests (admin only).
     *
     * @return list of pending vacation requests
     */
    @Override
    public List<VacationRequestDto> getPendingVacationRequests() {
        User currentUser = userService.getUserFromAuthentication();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view pending vacation requests");
        }

        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(VACATION_REQUEST_FILTER);
        filter.setParameter("isDeleted", false);

        List<VacationRequest> requests = vacationRequestRepository.findByStatusOrderByStartDateAsc(VacationRequestStatus.PENDING);

        session.disableFilter(VACATION_REQUEST_FILTER);

        return vacationRequestMapper.vacationRequestsToVacationRequestDtos(requests);
    }

    /**
     * Get a vacation request by id.
     *
     * @param requestId the id of the vacation request
     * @return the vacation request
     */
    @Override
    public VacationRequestDto getVacationRequestById(Long requestId) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(VACATION_REQUEST_FILTER);
        filter.setParameter("isDeleted", false);

        VacationRequest request = vacationRequestRepository.findOneById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vacation request not found"));

        session.disableFilter(VACATION_REQUEST_FILTER);

        User currentUser = userService.getUserFromAuthentication();
        if (currentUser.getRole() != Role.ADMIN && !request.getEmployee().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own vacation requests");
        }

        return vacationRequestMapper.vacationRequestToVacationRequestDto(request);
    }

    /**
     * Approve or reject a vacation request (admin only).
     *
     * @param requestId the id of the vacation request
     * @param vacationRequestUpdateDto the update data (status and rejection reason)
     * @return the updated vacation request
     */
    @Override
    @Transactional
    public VacationRequestDto updateVacationRequest(Long requestId, VacationRequestUpdateDto vacationRequestUpdateDto) {
        User currentUser = userService.getUserFromAuthentication();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can approve/reject vacation requests");
        }

        VacationRequest request = vacationRequestRepository.findOneById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vacation request not found"));

        if (request.getStatus() != VacationRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only update pending requests");
        }

        if (vacationRequestUpdateDto.getStatus() == VacationRequestStatus.APPROVED) {
            LocalDate currentDate = LocalDate.now();
            if (currentDate.isAfter(request.getStartDate()) || currentDate.isEqual(request.getStartDate())) {
                request.setStatus(VacationRequestStatus.REJECTED);
                request.setRejectionReason("Your vacation request was automatically rejected because the requested start date has already passed.");
                request.setProcessedBy(currentUser);
                VacationRequest updatedRequest = vacationRequestRepository.save(request);
                return vacationRequestMapper.vacationRequestToVacationRequestDto(updatedRequest);
            }
        }

        if (vacationRequestUpdateDto.getStatus() == VacationRequestStatus.REJECTED) {
            request.setRejectionReason(vacationRequestUpdateDto.getRejectionReason());
        }

        request.setStatus(vacationRequestUpdateDto.getStatus());
        request.setProcessedBy(currentUser);

        VacationRequest updatedRequest = vacationRequestRepository.save(request);
        return vacationRequestMapper.vacationRequestToVacationRequestDto(updatedRequest);
    }

    /**
     * Delete a vacation request (only if pending).
     *
     * @param requestId the id of the vacation request
     */
    @Override
    @Transactional
    public void deleteVacationRequest(Long requestId) {
        VacationRequest request = vacationRequestRepository.findOneById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vacation request not found"));

        User currentUser = userService.getUserFromAuthentication();
        if (currentUser.getRole() != Role.ADMIN && !request.getEmployee().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own vacation requests");
        }

        if (request.getStatus() != VacationRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only delete pending requests");
        }

        vacationRequestRepository.delete(request);
    }

    /**
     * Get the remaining vacation days for the authenticated user for a specific year.
     *
     * @param year the year
     * @return the number of remaining vacation days
     */
    @Override
    public Integer getRemainingVacationDays(Integer year) {
        User employee = userService.getUserFromAuthentication();
        return getRemainingVacationDaysForUser(employee.getId(), year);
    }

    /**
     * Get the total available vacation days for the authenticated user.
     *
     * @return the number of remaining vacation days
     */
    @Override
    public Integer getTotalVacationDays() {
        User employee = userService.getUserFromAuthentication();
        Integer numberOfChildren = employee.getNumberOfChildren();
        return baseVacationDays + numberOfChildren;
    }

    /**
     * Get the remaining vacation days for a specific user for a specific year.
     *
     * @param employeeId the id of the employee
     * @param year the year
     * @return the number of remaining vacation days
     */
    private Integer getRemainingVacationDaysForUser(Long employeeId, Integer year) {
        Integer totalAllowedDays = getTotalVacationDays();

        Integer usedDays = vacationRequestRepository.getTotalDaysUsedByEmployeeAndYear(employeeId, year);
        if (usedDays == null) {
            usedDays = 0;
        }

        Integer pendingDays = vacationRequestRepository.getTotalPendingDaysByEmployeeAndYear(employeeId, year);
        if (pendingDays == null) {
            pendingDays = 0;
        }

        return totalAllowedDays - usedDays - pendingDays;
    }

    /**
     * Calculate the number of business days between two dates (excluding weekends).
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return the number of business days
     */
    @Override
    public Integer calculateBusinessDays(LocalDate startDate, LocalDate endDate) {
        int businessDays = 0;
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            DayOfWeek dayOfWeek = current.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                businessDays++;
            }
            current = current.plusDays(1);
        }

        return businessDays;
    }

    /**
     * Get the count of active vacation requests (approved requests where current date is within the vacation range).
     *
     * @return the count of active vacation requests
     */
    @Override
    public Integer getActiveVacationsCount() {
        Long count = vacationRequestRepository.countActiveVacations();
        return count != null ? count.intValue() : 0;
    }

    /**
     * Get the total count of approved vacation requests.
     *
     * @return the count of approved vacation requests
     */
    @Override
    public Integer getTotalApprovedCount() {
        Long count = vacationRequestRepository.countTotalApproved();
        return count != null ? count.intValue() : 0;
    }

    /**
     * Automatically rejects pending vacation requests where the start date has passed.
     * This scheduled job runs daily at 8 AM (GMT+2) to ensure that vacation requests
     * with expired start dates are automatically rejected with a friendly message.
     */
    @Scheduled(cron = "0 0 8 * * *", zone = "GMT+2")
    @Transactional
    public void autoRejectRequestsWithExpiredStartDate() {
        List<VacationRequest> expiredRequests = vacationRequestRepository.findPendingRequestsWithExpiredStartDate();

        for (VacationRequest request : expiredRequests) {
            request.setStatus(VacationRequestStatus.REJECTED);
            request.setRejectionReason("Your vacation request was automatically rejected because the requested start date has already passed.");
            vacationRequestRepository.save(request);
        }
    }
}
