import com.example.StoreApplication;
import com.example.common.util.RedisUtil;
import com.example.service.QueryMessageService;
import com.example.service.StoreMessageService;
import com.example.storeapi.OfflineMessageSend;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = StoreApplication.class)
@Slf4j
public class TestMP {

    @Autowired
    private StoreMessageService service;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    QueryMessageService queryMessage;

    @Autowired
    OfflineMessageSend offlineMessageSend;

    @Test
    public void testStore(){
        redisUtil.setGroupMember(1, 1);
        redisUtil.setGroupMember(1, 2);
        redisUtil.setGroupMember(1, 3);
    }




}
