package it.pixel.grouping;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupBy<T> {
    @JsonIgnore
    private static final String ASC = "ASC";
    @JsonIgnore
    private static final String DESC = "DESC";
    @JsonIgnore
    private final HashMap<String, String> mapAlias = new HashMap<>();
    @JsonIgnore
    private HashMap<String, String> mapS = new HashMap<>();
    @JsonIgnore
    private HashMap<String, Long> mapN = new HashMap<>();
    @JsonIgnore
    private String defaultCol;

    private HashMap<String, String> padre = new HashMap<>();
    private List<T> listaFigli = new ArrayList<>();

    public GroupBy() {
    }

    public void clear() {
        mapS.clear();
        mapN.clear();
    }

    public void setDefault(String defaultCol) {
        this.defaultCol = defaultCol;
    }

    public GroupBy<T> addField(String name, String value) {
        mapS.put(name, value);
        mapAlias.put(name, name);

        return this;
    }

    public GroupBy<T> addField(String name, Long value) {
        mapN.put(name, value);
        mapAlias.put(name, name);
        return this;
    }

    public GroupBy<T> addField(String name, String value, String alias) {
        mapS.put(name, value);
        mapAlias.put(alias, name);
        return this;
    }

    public GroupBy<T> addField(String name, Long value, String alias) {
        mapN.put(name, value);
        mapAlias.put(alias, name);
        return this;
    }

    public HashMap<String, String> getMapS() {
        return this.mapS;
    }

    public HashMap<String, Long> getMapN() {
        return this.mapN;
    }

    public List<T> getListaFigli() {
        return this.listaFigli;
    }

    public void setMapS(HashMap<String, String> mapS) {
        this.mapS = mapS;
    }

    public void setMapN(HashMap<String, Long> mapN) {
        this.mapN = mapN;
    }

    public void setListaFigli(List<T> listaFigli) {
        this.listaFigli = listaFigli;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof GroupBy)) return false;
        final GroupBy<?> other = (GroupBy<?>) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$mapS = this.getMapS();
        final Object other$mapS = other.getMapS();
        if (this$mapS == null ? other$mapS != null : !this$mapS.equals(other$mapS)) return false;
        final Object this$mapL = this.getMapN();
        final Object other$mapL = other.getMapN();
        if (this$mapL == null ? other$mapL != null : !this$mapL.equals(other$mapL)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof GroupBy;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $mapS = this.getMapS();
        result = result * PRIME + ($mapS == null ? 43 : $mapS.hashCode());
        final Object $mapL = this.getMapN();
        result = result * PRIME + ($mapL == null ? 43 : $mapL.hashCode());
        return result;
    }


}

