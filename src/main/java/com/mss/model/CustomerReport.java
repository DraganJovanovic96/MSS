package com.mss.model;

import com.mss.enumeration.CustomerReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import java.time.Instant;

/**
 * Entity representing a customer report created by receptionists.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "customer_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE customer_reports SET deleted = true WHERE id=?")
@FilterDef(name = "deletedCustomerReportFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedCustomerReportFilter", condition = "deleted = :isDeleted")
public class CustomerReport extends BaseEntity<Long> {

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private String issueDescription;

    private String photoUrls;

    @Enumerated(EnumType.STRING)
    private CustomerReportStatus status;

    private Instant reviewedAt;

    private Instant assignedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "service_id")
    private Service service;
}
