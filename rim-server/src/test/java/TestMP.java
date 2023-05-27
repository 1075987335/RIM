import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;
import com.example.server.ServerApplication;
import com.example.server.service.SendOfflineMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ServerApplication.class)
@Slf4j
public class TestMP {

    @Autowired
    SendOfflineMessage sendOfflineMessage;

    @Test
    public void test1(){

        IM_Message message = new IM_Message();
        Header header = new Header();
        header.setUID(1L);
        message.setHeader(header);
        sendOfflineMessage.send(message);

    }
}
