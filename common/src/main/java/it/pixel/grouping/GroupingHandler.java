package it.pixel.grouping;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: common-library
 * Author: Michele
 * File: GroupingHandler
 * Creation: 10/07/2022
 */
public class GroupingHandler<T> {

    private static final String ASC = "ASC";
    private static final String DESC = "DESC";
    private String defaultCol;
    private HashMap<String, String> mapAlias = new HashMap<>();
    private HashMap<String, String> mapS = new HashMap<>();
    private HashMap<String, Long> mapN = new HashMap<>();

    private List<T> source;
    private List<GroupBy<T>> object;
    private List<String> fields;

    public GroupingHandler(List<T> source, String... fields) {
        this.source = source;
        this.fields = Arrays.stream(fields).toList();
    }

    public void clear() {
        mapS.clear();
        mapN.clear();
    }

    public void setDefault(String defaultCol) {
        this.defaultCol = defaultCol;
    }

    public void addField(String name, String value) {
        mapS.put(name, value);
        mapAlias.put(name, name);
    }

    public void addField(String name, Long value) {
        mapN.put(name, value);
        mapAlias.put(name, name);
    }

    public void addField(String name, String value, String alias) {
        mapS.put(name, value);
        mapAlias.put(alias, name);
    }

    public void addField(String name, Long value, String alias) {
        mapN.put(name, value);
        mapAlias.put(alias, name);
    }

    private Comparator<GroupBy<?>> getComparator(String orderColumn, String orderDirection) {

        Comparator<String> sComparator = ASC.equals(orderDirection) ? Comparator.naturalOrder() : Comparator.reverseOrder();
        Comparator<Long> nComparator = ASC.equals(orderDirection) ? Comparator.naturalOrder() : Comparator.reverseOrder();

        String field = mapAlias.get(orderColumn);
        Map.Entry<String, String> s = mapS.entrySet().stream().filter(x -> x.getKey().equals(field)).findFirst().orElse(null);
        Map.Entry<String, Long> n = mapN.entrySet().stream().filter(x -> x.getKey().equals(field)).findFirst().orElse(null);

        if (s == null && n == null) {
            // che stai a fa?
            // applico quello di default se c'e'
            return Comparator.comparing(x -> x.getMapS().get(defaultCol), sComparator);
        } else {
            if (s != null) {
                return Comparator.comparing(x -> x.getMapS().get(field), sComparator);
            } else {
                return Comparator.comparing(x -> x.getMapN().get(field), nComparator);
            }
        }


    }

    public List<GroupBy<T>> getResult() {
        List<GroupBy<T>> toReturn = new ArrayList<>();
        for (GroupBy<T> uff : object) {
            GroupBy<T> obj = new GroupBy<>();
            uff.getMapS().entrySet().iterator().forEachRemaining(x -> obj.addField(x.getKey(), x.getValue()));
            uff.getMapN().entrySet().iterator().forEachRemaining(x -> obj.addField(x.getKey(), String.valueOf(x.getValue())));
        }
        return toReturn;
    }

    public List<GroupBy<T>> getGroupedList() {

        Map<GroupBy<T>, List<T>> a = source
                .stream()
                .collect(Collectors.groupingBy(x -> {
                    ParameterizedType t = (ParameterizedType) this.getClass().getGenericSuperclass();
                    Class<?> clazz = t.getActualTypeArguments()[0].getClass();

                    GroupBy<T> group = new GroupBy<>();
                    try {
                        for (String field : fields) {
                            group.addField(field, clazz.getField(field).toGenericString());
                        }
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    return group;

                }));


        List<GroupBy<T>> tot = new ArrayList<>();
        a.entrySet().iterator().forEachRemaining(x -> {
            x.getKey().setListaFigli(x.getValue());
            tot.add(x.getKey());
        });
        return tot;
    }

}
