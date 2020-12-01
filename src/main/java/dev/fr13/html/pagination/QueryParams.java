package dev.fr13.html.pagination;

import java.util.Map;

public interface QueryParams {

    Map<String, Object> getParams();

    void setPaginationParam(int val);
}
