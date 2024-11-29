package com.mss.repository;


import com.mss.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing tokens.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    /**
     * Retrieves a list of valid tokens associated with a user.
     *
     * @param id the ID of the user
     * @return a list of valid tokens
     */
    @Query(value = """
            select t from Token t inner join User u\s
            on t.user.id = u.id\s
            where u.id = :id and (t.expired = false or t.revoked = false)\s
            """)
    List<Token> findAllValidTokenByUser(Long id);

    /**
     * Retrieves a token by its value.
     *
     * @param token the token value
     * @return an Optional containing the token, or an empty Optional if not found
     */
    Optional<Token> findByToken(String token);

    /**
     * Finds all tokens that are marked as deleted.
     *
     * @return A list of tokens that are marked as deleted.
     */
    @Query("SELECT t FROM Token t WHERE t.deleted = true")
    List<Token> findAllDeletedTokens();

    /**
     * Permanently deletes a Token entity from the database by its ID.
     *
     * <p>This method executes a DELETE operation on the Token entity,
     * removing the record with the specified ID from the database. This operation
     * is not reversible and will permanently remove the entity.</p>
     *
     * @param tokenId the ID of the Token entity to be deleted
     */
    @Modifying
    @Query("DELETE FROM Token t WHERE t.id = :tokenId")
    void permanentlyDeleteTokenById(Long tokenId);
}
