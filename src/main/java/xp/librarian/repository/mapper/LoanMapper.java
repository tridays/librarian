package xp.librarian.repository.mapper;

import java.util.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import xp.librarian.model.dto.Loan;

/**
 * @author xp
 */
@Mapper
public interface LoanMapper {

    int insert(Loan loan);

    int update(@Param("where") Loan where,
               @Param("set") Loan set);

    List<Loan> select(@Param("where") Loan where,
                      @Param("offset") Long offset,
                      @Param("limits") Integer limits);

}
