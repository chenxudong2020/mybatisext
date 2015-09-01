package com.github.mybatisext.test.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.mybatisext.activerecord.Table;
import com.github.mybatisext.annotation.TableName;
import com.github.mybatisext.helper.Page;
import com.github.mybatisext.test.entity.User;


/**
 *
 * UserMapper数据库操作接口类
 *
 */
@TableName(name = "T_USER", type = User.class)
public interface UserMapper extends Table<User, Long> {


	List<User> selectByName( @Param("name") String name );


	Page<User> selectByNamePaging( Page<?> page, @Param("name") String name );

}