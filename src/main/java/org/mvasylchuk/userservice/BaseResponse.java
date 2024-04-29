package org.mvasylchuk.userservice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse<T> {
    private final T data;
    private String errorMessage;
}
