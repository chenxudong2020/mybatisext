package com.ext_ext.mybatisext.test.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ext_ext.mybatisext.activerecord.Table;
import com.ext_ext.mybatisext.annotation.TableName;
import com.ext_ext.mybatisext.helper.Page;
import com.ext_ext.mybatisext.test.entity.User;


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