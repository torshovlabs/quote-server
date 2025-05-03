package com.torshovlabs.quote.dao;

import com.torshovlabs.quote.domain.GroupMembership;
import java.util.List;
import java.util.Optional;

public interface GroupMembershipDAO {

    GroupMembership save(GroupMembership groupMembership);

    Optional<GroupMembership> findById(Long id);

    Optional<GroupMembership> findByUserAndGroup(String userId, Long groupId);

    List<GroupMembership> findByUser(String userId);

    List<GroupMembership> findByGroup(Long groupId);

    Optional<GroupMembership> findByGroupAndCanQuote(Long groupId, Boolean canQuote);

    List<GroupMembership> findByGroupOrderByQueueNumber(Long groupId);

    void updateCanQuoteStatus(Long membershipId, Boolean canQuote);

    void resetAllCanQuoteForGroup(Long groupId, Boolean canQuote);

    void deleteById(Long id);

    boolean existsByUserAndGroup(String userId, Long groupId);

    int getMaxQueueNumberForGroup(Long groupId);
}