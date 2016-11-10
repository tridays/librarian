package xp.librarian.repository.impl;

import java.util.*;
import java.util.stream.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import xp.librarian.repository.GlobalVarDao;
import xp.librarian.repository.mapper.GlobalVarMapper;

/**
 * @author xp
 */
@Repository
public class GlobalVarDaoImpl implements GlobalVarDao {

    @Autowired
    private GlobalVarMapper globalVarMapper;

    private Map<String, String> vars;

    public void reload() {
        vars = globalVarMapper.selectAll().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue().get("value")));
    }

    public Map<String, String> getAll() {
        return vars;
    }

    public String get(String key) {
        return vars.get(key);
    }

}
