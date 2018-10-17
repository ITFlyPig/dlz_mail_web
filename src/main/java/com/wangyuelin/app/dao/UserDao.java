package com.wangyuelin.app.dao;

import com.wangyuelin.app.bean.User;
import com.wangyuelin.app.config.mybatis.baseMapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao  extends BaseMapper<User> {

    @Select("select * from stu where id=#{id}")
    User getUserById(@Param("id") int id);



}
