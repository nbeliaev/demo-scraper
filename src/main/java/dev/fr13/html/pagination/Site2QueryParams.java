package dev.fr13.html.pagination;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Qualifier("site2")
public class Site2QueryParams implements QueryParams {
    private static final String PAGINATION_PARAM = "page";
    private final Map<String, Object> params = new LinkedHashMap<>(2);

    public Site2QueryParams() {
        params.put("countonpage", "120");
    }

    @Override
    public Map<String, Object> getParams() {
        return params;
    }

    @Override
    public void setPaginationParam(int val) {
        params.put(PAGINATION_PARAM, val);
    }
}
