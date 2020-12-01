package dev.fr13.html.pagination;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Qualifier("site3")
public class Site3QueryParams implements QueryParams {
    private static final String PAGINATION_PARAM = "PAGEN_1";
    private final Map<String, Object> params = new LinkedHashMap<>(3);

    public Site3QueryParams() {
        params.put("alfaction", "coutput");
        params.put("alfavalue", "20");
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
