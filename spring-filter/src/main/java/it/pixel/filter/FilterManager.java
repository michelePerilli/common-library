package it.pixel.filter;

import it.pixel.filter.annotation.OnlyNotDeleted;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilterManager { // package-private class

    public enum Status {ENABLED, DISABLED}

    public static final String FIELD_FLAG_ELIMINATO = "FLAG_ELIMINATO";
    public static final String FIELD_FLAG_ELIMINATO_VALUE = "N";

    private Status filtersStatus = Status.DISABLED;
    private final Map<String, Boolean> filterableEntity;

    private static FilterManager instance;

    private FilterManager() {
        filterableEntity = fetchForFilterableEntities();
    }


    public static FilterManager getInstance() {
        if (instance == null) instance = new FilterManager();
        return instance;
    }

    public Status getFiltersStatus() {

        return filtersStatus;
    }

    public void setFiltersStatus(Status status) {
        filtersStatus = status;
    }

    public Map<String, Boolean> getFilterableEntity() {
        return new HashMap<>(filterableEntity);
    }

    private static Map<String, Boolean> fetchForFilterableEntities() {

        return new Reflections("it.mef").getSubTypesOf(BaseEntity.class).stream().filter(clazz -> clazz.isAnnotationPresent(Table.class)).collect(Collectors.toMap(FilterManager::getTableName, FilterManager::checkIfFilterable));
    }

    private static <T extends BaseEntity> String getTableName(Class<T> entity) {
        return entity.getAnnotation(Table.class).name();
    }

    private static <T extends BaseEntity> Boolean checkIfFilterable(Class<T> entity) {

        boolean hasCorrectAnnotation = entity.isAnnotationPresent(OnlyNotDeleted.class);

        boolean hasCorrectColumn = Arrays.stream(entity.getDeclaredFields()).filter(x -> x.isAnnotationPresent(Column.class)).anyMatch(x -> Objects.equals(x.getAnnotation(Column.class).name(), FIELD_FLAG_ELIMINATO));

        return hasCorrectAnnotation && hasCorrectColumn;
    }


}
