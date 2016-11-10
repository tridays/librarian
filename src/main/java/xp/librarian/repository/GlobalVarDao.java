package xp.librarian.repository;

import java.util.*;

/**
 * @author xp
 */
public interface GlobalVarDao {

    void reload();

    String get(String key);

    Map<String, String> getAll();

}
