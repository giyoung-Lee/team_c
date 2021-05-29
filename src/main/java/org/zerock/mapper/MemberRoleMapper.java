package org.zerock.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.zerock.domain.MemberRole;

import java.util.List;
import java.util.Map;

@Mapper
public interface MemberRoleMapper {
    List<MemberRole> selectMemberRoleByEmail(Map<String, Object> params);
    int insertMemberRole(MemberRole memberRole);
}