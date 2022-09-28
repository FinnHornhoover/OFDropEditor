package finnhh.oftools.dropeditor.model;

import java.util.List;

public record NanoInfo(int id, String name, String type, String iconName,
                       List<NanoPowerInfo> powers) implements InfoEnum {
}
