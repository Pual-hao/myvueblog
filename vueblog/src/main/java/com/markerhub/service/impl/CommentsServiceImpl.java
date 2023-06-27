package com.markerhub.service.impl;

import com.markerhub.entity.Comments;
import com.markerhub.mapper.CommentsMapper;
import com.markerhub.service.CommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author anonymous
 * @since 2023-06-27
 */
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements CommentsService {

}
