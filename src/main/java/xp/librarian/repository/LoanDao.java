package xp.librarian.repository;

import java.util.*;

import xp.librarian.model.dto.Loan;

/**
 * @author xp
 */
public interface LoanDao {

    int add(Loan loan);

    int update(Loan where, Loan set);

    Loan get(Long loanId);

    Loan get(Loan where);

    List<Loan> gets(Loan where, Long offset, Integer limits);

}
