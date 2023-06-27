package com.markerhub.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 
 * </p>
 *
 * @author anonymous
 * @since 2023-06-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_comments")
public class Comments implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long blogId;

    @NotBlank(message = "内容不能为空")
    private String description;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDateTime created;


}
