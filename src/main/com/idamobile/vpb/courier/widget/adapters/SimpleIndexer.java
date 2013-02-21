package com.idamobile.vpb.courier.widget.adapters;

import android.widget.SectionIndexer;

import java.util.ArrayList;
import java.util.List;

public class SimpleIndexer<T> implements SectionIndexer {

    public interface SectionRetriever<T> {
        Object getSectionFrom(T item);
    }

    private SectionRetriever<T> sectionRetriever;
    private SectionListAdapter<T> adapter;
    private Integer[] sectionPositions;

    public SimpleIndexer(SectionRetriever<T> sectionRetriever, SectionListAdapter<T> adapter) {
        this.sectionRetriever = sectionRetriever;
        this.adapter = adapter;
    }

    @Override
    public Object[] getSections() {
        List<Object> sections = new ArrayList<Object>();
        List<Integer> positions = new ArrayList<Integer>();
        int pos = 0;
        for (T item : adapter.getReadOnlyItems()) {
            Object section = sectionRetriever.getSectionFrom(item);
            if (!sections.contains(section)) {
                sections.add(section);
                positions.add(pos);
                pos++;
            }
            pos++;
        }
        Object[] sectionsArrs = sections.toArray(new Object[sections.size()]);
        sectionPositions = positions.toArray(new Integer[positions.size()]);
        return sectionsArrs;
    }

    @Override
    public int getPositionForSection(int section) {
        return sectionPositions[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < sectionPositions.length; i++) {
            if (sectionPositions[i] > position) {
                return i;
            }
        }
        return -1;
    }

}