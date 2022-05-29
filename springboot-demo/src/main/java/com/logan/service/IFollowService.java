package com.logan.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.logan.dto.Result;
import com.logan.entity.Follow;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IFollowService extends IService<Follow> {

    Result follow(Long followUserId, Boolean isFollow);

    Result isFollow(Long followUserId);

    Result followCommons(Long id);
}
