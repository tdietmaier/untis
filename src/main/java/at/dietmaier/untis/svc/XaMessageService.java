package at.dietmaier.untis.svc;

import at.dietmaier.untis.kafka.KafkaProxy;
import at.dietmaier.untis.persistence.MessageEntity;
import at.dietmaier.untis.persistence.MessageRepository;
import at.dietmaier.untis.persistence.QueueEntity;
import at.dietmaier.untis.persistence.QueueRepository;
import com.atomikos.datasource.xa.XID;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class XaMessageService {
    @Autowired
    private MessageRepository messageRepo;
    @Autowired
    private KafkaProxy kafka;
    @Autowired
    private JtaTransactionManager transactionManager;
    @Autowired
    private AtomikosDataSourceBean untisDataSource;

    public XaMessageService() {
    }

    public void save(Message msg) {
        XAConnection xaCon = null;
        XAResource xaRes = null;
        Xid xid = null;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            XADataSource xaDS = untisDataSource.getXaDataSource();
            int ret;
            xaCon = xaDS.getXAConnection();
            xaRes = xaCon.getXAResource();
            con = xaCon.getConnection();
            stmt = con.prepareStatement("insert into message_entity (id, message) values(?,?)");
            stmt.setLong(1, msg.getId());
            stmt.setString(2, msg.getText());
            xid = new XID(UUID.randomUUID().toString(),untisDataSource.getUniqueResourceName());
            xaRes.start(xid, XAResource.TMNOFLAGS);
            stmt.executeUpdate();
            xaRes.end(xid, XAResource.TMSUCCESS);
            ret = xaRes.prepare(xid);
            if (ret == XAResource.XA_OK) {
                kafka.send(msg);
                xaRes.commit(xid, false);
            }}
        catch (XAException | SQLException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }finally {
            try {
                stmt.close();
                con.close();
                xaCon.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
