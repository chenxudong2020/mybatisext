package com.ext.mybatisext.test.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.ext.mybatisext.activerecord.Table;
import com.ext.mybatisext.annotation.TableName;
import com.ext.mybatisext.helper.Page;
import com.ext.mybatisext.mapper.CommonMapper;
import com.ext.mybatisext.test.entity.User;


/**
 *
 * UserMapper数据库操作接口类
 *
 */
@TableName(name = "T_USER", type = User.class)
public interface UserMapper extends Table<User, Long>, CommonMapper<User> {


	List<User> selectByName( @Param("name") String name );

	@Results({
			@Result(column="id",property="id"),
			@Result(column="user_name",property="USER_Name"),
			@Result(column="user_age",property="userAGE")
	})
	@Select("select * from t_user where user_name = #{name}")
	List<User> selectByName2( @Param("name") String name );


	Page<User> selectByNamePaging( Page<?> page, @Param("name") String name );

}