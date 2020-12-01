package dev.fr13.repository;

import dev.fr13.domain.MenuItem;
import dev.fr13.domain.WebSite;
import dev.fr13.dto.TotalMenuItemStatisticDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends CrudRepository<MenuItem, Long> {

    Optional<MenuItem> findBySourceAndUrn(WebSite source, String urn);

    List<MenuItem> findAllBySourceOrderByNameAsc(WebSite source);

    List<MenuItem> findAllBySourceAndActiveTrue(WebSite source);

    @Query("SELECT " +
                "new dev.fr13.dto.TotalMenuItemStatisticDto(m.source, count(distinct mi.urn), count(distinct m.urn), count(p.id)) " +
            "FROM " +
                " MenuItem m " +
            "LEFT JOIN MenuItem mi ON mi.id = m.id AND mi.active = TRUE " +
            "LEFT JOIN Product p ON m.id = p.menuItem " +
            "GROUP BY m.source")
    List<TotalMenuItemStatisticDto> getTotalStatistic();
}
