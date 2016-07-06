package skychat;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class SCMsg
{
    public final String msgFull;

    public SCMsg(String msgFull)
    {
        this.msgFull = msgFull;
    }

    public static SCMsg loadFrom(ByteArrayDataInput in)
    {
        String msg = in.readUTF();
        return new SCMsg(msg);
    }

    public void writeTo(ByteArrayDataOutput out)
    {
        out.writeUTF(msgFull);
    }
}
