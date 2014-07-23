package metaq;

import com.taobao.metamorphosis.client.extension.spring.DefaultMessageListener;
import com.taobao.metamorphosis.client.extension.spring.MetaqMessage;

public class DataRecordMessageListener extends DefaultMessageListener<Record> {

	@Override
	public void onReceiveMessages(MetaqMessage<Record> msg) {
		System.out.println(msg.getBody());
	}

}
