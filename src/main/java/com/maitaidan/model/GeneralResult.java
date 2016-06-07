package com.maitaidan.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Administrator on 2016/6/7.
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GeneralResult<T> {

    private boolean ret;
    private T data;
    private String msg;
}
