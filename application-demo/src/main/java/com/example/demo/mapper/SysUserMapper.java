package com.example.demo.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.demo.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户管理 Mapper 接口
 * </p>
 *
 * @author mark zhou
 * @since 2025-11-12
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
