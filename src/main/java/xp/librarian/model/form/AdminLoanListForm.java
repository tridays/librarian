package xp.librarian.model.form;

import java.io.*;

import lombok.Data;
import xp.librarian.model.dto.Loan;

/**
 * @author xp
 */
@Data
public class AdminLoanListForm implements Serializable {

    private static final long serialVersionUID = -438526386140204211L;

    private Long userId;

    private Loan.Status status;

}
