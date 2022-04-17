package com.ext.mybatisext.test.mapper;

import com.ext.mybatisext.test.entity.User;
import com.gitee.fastmybatis.core.mapper.CrudMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 *
 * UserMapper数据库操作接口类
 *
 */

public interface UserMapper extends CrudMapper<User,Long> {


	List<User> selectByName( @Param("name") String name );

	@Update("${selSql}")
	int updateA(@Param("selSql") String selSql);



}