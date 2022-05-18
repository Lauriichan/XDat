package me.lauriichan.data.xdat;

@XEntity(id = 0)
public final class Example {

    @XData(ioId = XDatStringIO.ID, ioArgs = {
        "size",
        "32"
    })
    public String name;

    @XData
    public int id;

}
