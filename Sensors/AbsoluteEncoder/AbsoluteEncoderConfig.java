package POPLib.Sensors.AbsoluteEncoder;

public class AbsoluteEncoderConfig {
    public final int id;
    public final double offset;
    public final boolean inversion;

    public AbsoluteEncoderConfig(int id, double offset, boolean inversion) {
        this.id = id;
        this.offset = offset;
        this.inversion = inversion;
    }

    public AbsoluteEncoder generateAbsoluteEncoder() {
        return new AbsoluteEncoder(id, offset, inversion);
    }

}
