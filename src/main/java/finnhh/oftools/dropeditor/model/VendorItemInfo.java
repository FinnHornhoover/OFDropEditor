package finnhh.oftools.dropeditor.model;

public record VendorItemInfo(ItemInfo itemInfo, int npc, int vendorOrder, int vendorPrice) implements InfoEnum {
}
