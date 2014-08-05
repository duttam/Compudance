
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

@ManagedBean
@SessionScoped
public class ExceptionBean {

    public void throwRuntimeException(ActionEvent actionEvent) {
        throw new RuntimeException("peek-a-boo");
    }

    public void throwSQLException() throws SQLException {
        throw new SQLException("DB fail");
    }

}