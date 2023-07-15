package page.info.edit;


import common.battle.data.CustomUnit;
import common.battle.data.PCoin;
import common.util.pack.Background;
import common.util.stage.Music;
import common.util.unit.Form;
import page.JBTN;
import page.Page;
import page.SupPage;
import utilpc.UtilPC;

import javax.swing.*;
import java.util.Vector;

public class PCoinEditPage extends Page implements SwingEditor.EditCtrl.Supplier {

    private static final long serialVersionUID = 1L;

    private final JBTN back = new JBTN(0, "back");
    private final JBTN add = new JBTN(0, "add");
    private final JBTN rem = new JBTN(0, "rem");
    private final JList<String> coin = new JList<>();
    private final JScrollPane jspc = new JScrollPane(coin);
    private final boolean editable;
    private final CustomUnit unit;
    private final PCoinEditTable2 pcet;
    private final JBTN info = new JBTN(0, "so i've got this new anime plot");
    boolean changing = false;

    public PCoinEditPage(Page p, Form u, boolean edi) {
        super(p);
        unit = (CustomUnit) u.du;
        editable = edi;
        pcet = new PCoinEditTable2(this, unit, editable);

        ini();
        resized();
    }

    @Override
    protected JButton getBackButton() {
        return back;
    }

    @Override
    protected void resized(int x, int y) {
        setBounds(0, 0, x, y);
        set(back, x, y, 0, 0, 200, 50);
        set(jspc, x, y, 50, 300, 300, 500);
        set(add, x, y, 50, 800, 150, 50);
        set(rem, x, y, 200, 800, 150, 50);
        set(pcet, x, y, 400, 150, 600, 1200);
        set(info, x, y, 850, 850, 200, 50);
    }

    private void addListeners() {
        back.addActionListener(arg0 -> changePanel(getFront()));

        add.addActionListener(arg0 -> {
            if (changing)
                return;
            changing = true;
            if (unit.pcoin == null)
                unit.pcoin = new PCoin(unit);

            int slot = unit.pcoin.info.size();
            unit.pcoin.info.add(new int[]{ slot + 1, 10, 0, 0, 0, 0, 0, 0, 0, 0, slot + 1, 8, 1, -1 });
            unit.pcoin.max = new int[slot + 1];
            for(int i = 0; i < slot; i++)
                unit.pcoin.max[i] = unit.pcoin.info.get(i)[1];
            unit.pcoin.max[slot] = 10;

//            for (int i = 0; i < slot; i++)
//                if (unit.pcoin.info.get(i)[0] == slot + 1) {
//                    PCoinEditTable pc = pCoinEdits.get(i);
//
//                    pc.setData();
//                    pc.randomize();
//                }
            setCoinTypes();
            changing = false;
        });

        //PCoin Structure:
        //[0] = ability identifier, [1] = max lv, [2,4,6,8] = min lv values, [3,5,7,9] = max lv values, [10,11,12] = ???

        rem.addActionListener(arg0 -> {
            if (changing)
                return;
            changing = true;
            unit.pcoin.info.remove(coin.getSelectedIndex());
            unit.pcoin.max = new int[unit.pcoin.info.size()];
            for (int i = 0; i < unit.pcoin.info.size(); i++)
                unit.pcoin.max[i] = unit.pcoin.info.get(i)[1];
            if (unit.pcoin.info.size() == 0)
                unit.pcoin = null;
            setCoinTypes();
            changing = false;
        });

        coin.addListSelectionListener(x -> {
            if (changing)
                return;
            changing = true;
            boolean selected = !coin.isSelectionEmpty();
            rem.setEnabled(editable && selected);
            pcet.setData(coin.getSelectedIndex());
            changing = false;
        });
    }

    protected void setCoinTypes() {
        setCoins();
    }

    //Changes the other talent indexes once a talent is removed from the list
    protected void removed() {

    }

    private void ini() {
        add(back);
        add(add);
        add(rem);
        add(jspc);
        add(pcet);
        coin.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addListeners();
        setCoins();
    }

    private void setCoins() {
        int ind = coin.getSelectedIndex();
        if (unit.pcoin != null) {
            Vector<String> talents = new Vector<>();
            PCoin p = unit.pcoin;
            p.update();
            for (int i = 0; i < p.max.length; i++)
                talents.add("talent " + i + ": " + UtilPC.getPCoinAbilityText(p, i));
            coin.setListData(talents);
            coin.setSelectedIndex(Math.min(ind, talents.size() - 1));
        } else {
            coin.setListData(new String[0]);
        }

        add.setEnabled(editable && (unit.pcoin == null || unit.pcoin.info.size() < 6));
        rem.setEnabled(editable && coin.getSelectedIndex() != -1);
        pcet.setData(coin.getSelectedIndex());
    }

    @Override
    public SupPage<Background> getBGSup(SwingEditor.IdEditor<Background> edi) {
        return null;
    }

    @Override
    public SupPage<Music> getMusicSup(SwingEditor.IdEditor<Music> edi) {
        return null;
    }

    @Override
    public SupPage<?> getEntitySup(SwingEditor.IdEditor<?> edi) {
        return null;
    }
}