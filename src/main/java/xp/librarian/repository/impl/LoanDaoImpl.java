package xp.librarian.repository.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.NonNull;
import xp.librarian.model.dto.Loan;
import xp.librarian.repository.LoanDao;
import xp.librarian.repository.mapper.LoanMapper;

/**
 * @author xp
 */
@Repository
public class LoanDaoImpl implements LoanDao {

    @Autowired
    private LoanMapper loanMapper;

    public int add(@NonNull Loan loan) {
        return loanMapper.insert(loan);
    }

    public int update(@NonNull Loan where,
                      @NonNull Loan set) {
        return loanMapper.update(where, set);
    }

    public Loan get(@NonNull Long loanId) {
        return get(new Loan().setId(loanId));
    }

    public Loan get(@NonNull Loan where) {
        return gets(where, 0L, 1).stream()
                .findFirst().orElse(null);
    }

    public List<Loan> gets(@NonNull Loan where,
                           @NonNull Long offset,
                           @NonNull Integer limits) {
        return loanMapper.select(where, offset, limits);
    }

}
