package xp.librarian.model.form;

import java.io.*;

import lombok.Data;
import xp.librarian.model.dto.Loan;

/**
 * @author xp
 */
@Data
public class LendListForm implements Serializable {

    private static final long serialVersionUID = -1065529118164949784L;

    private Loan.Status status;

}
