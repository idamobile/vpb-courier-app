package com.idamobile.vpb.courier.widget.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.idamobile.vpb.courier.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class SectionListAdapter<T> extends BaseAdapter {

    private static final int ITEM_TYPE_NORMAL = 0;
    private static final int ITEM_TYPE_HEADER = 1;

    private boolean notifyOnChange = true;
    private List<T> allItems = new ArrayList<T>();
    private List<T> items = new ArrayList<T>();
    private List<T> readOnlyItems = Collections.unmodifiableList(items);

    private Object[] sections;
    private int[] sectionPositions;
    private Comparator<T> itemsComparator;
    private SectionIndexer sectionIndexer;

    private String filterQuery;

    public void setNotifyOnChange(boolean notifyOnChange) {
        this.notifyOnChange = notifyOnChange;
    }

    public void setItemsComparator(Comparator<T> itemsComparator) {
        this.itemsComparator = itemsComparator;
    }

    public void setSectionIndexer(SectionIndexer sectionIndexer) {
        this.sectionIndexer = sectionIndexer;
    }

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
        filter();
        refreshSections();
        notifyDatasetIfNeeded();
    }

    public String getFilterQuery() {
        return filterQuery;
    }

    protected List<T> filterItems(String query) {
        List<T> result = new ArrayList<T>(allItems.size());
        for (T item : allItems) {
            if (isItemMatched(item, query)) {
                result.add(item);
            }
        }
        return result;
    }

    protected boolean isItemMatched(T item, String query) {
        return true;
    }

    private void filter() {
        items.clear();
        if (TextUtils.isEmpty(filterQuery)) {
            items.addAll(allItems);
        } else {
            items.addAll(filterItems(filterQuery));
        }
    }

    public void clear() {
        allItems.clear();
        filter();
        refreshSections();
        notifyDatasetIfNeeded();
    }

    public void replaceAll(List<T> newItems) {
        allItems.clear();
        addItems(newItems);
    }

    public void addItem(T item) {
        allItems.add(item);
        sort(allItems);
        filter();
        refreshSections();
        notifyDatasetIfNeeded();
    }

    public void addItems(List<T> items) {
        for (T item : items) {
            allItems.add(item);
        }
        sort(allItems);
        filter();
        refreshSections();
        notifyDatasetIfNeeded();
    }

    public void removeItem(T item) {
        allItems.remove(item);
        filter();
        refreshSections();
        notifyDatasetIfNeeded();
    }

    private void sort(List<T> items) {
        if (itemsComparator != null) {
            Collections.sort(items, itemsComparator);
        }
    }

    public boolean isLastInSection(int position) {
        if (position >= getCount() - 1) {
            return true;
        } else {
            if (sectionPositions != null) {
                for (int sectionPosition : sectionPositions) {
                    if (sectionPosition == position + 1) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public int getIndexOf(T item) {
        int count = getCount();
        for (int i = 0; i < count; i++) {
            if (!isSectionPosition(i)) {
                if (getItem(i) == item) {
                    return i;
                }
            }
        }
        return -1;
    }

    public List<T> getReadOnlyItems() {
        return readOnlyItems;
    }

    private void refreshSections() {
        sections = getSections();
        if (sections != null) {
            sectionPositions = new int[sections.length];
            for (int i = 0; i < sections.length; i++) {
                sectionPositions[i] = getPositionForSection(i);
            }
        } else {
            sectionPositions = null;
        }
    }

    public boolean isSectionPosition(int position) {
        return getCachedSectionForPosition(position) != -1;
    }

    public int getCachedSectionForPosition(int position) {
        if (sectionPositions != null) {
            for (int i = 0; i < sectionPositions.length; i++) {
                if (sectionPositions[i] == position) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getSectionsCountBeforePosition(int position) {
        int count = 0;
        if (sectionPositions != null) {
            for (int sectionPosition : sectionPositions) {
                if (sectionPosition > position) {
                    break;
                }
                count++;
            }
        }
        return count;
    }

    private void notifyDatasetIfNeeded() {
        if (notifyOnChange) {
            notifyDataSetChanged();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return isSectionPosition(position) ? ITEM_TYPE_HEADER : ITEM_TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return items.size() + (sections != null ? sections.length : 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getItem(int position) {
        if (isSectionPosition(position)) {
            throw new IllegalArgumentException("position \"" + position + "\" corresponds to section header!");
        }
        return (T) getInternalItem(position);
    }

    private Object getInternalItem(int position) {
        int sectionIndex = getCachedSectionForPosition(position);
        return sectionIndex != -1
                ? sections[sectionIndex]
                        : items.get(position - getSectionsCountBeforePosition(position));
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == ITEM_TYPE_NORMAL;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        int itemType = getItemViewType(position);
        Object item = getInternalItem(position);

        switch (itemType) {
        case ITEM_TYPE_HEADER:
            if (convertView == null) {
                convertView = createHeaderView(item, parent, inflater);
            }
            bindHeaderView(convertView, item);
            break;
        case ITEM_TYPE_NORMAL:
            if (convertView == null) {
                convertView = createNormalView((T) item, parent, inflater);
            }
            bindNormalView(convertView, (T) item);
            break;
        default:
            //do nothing
            break;
        }

        return convertView;
    }

    protected abstract View createNormalView(T item, ViewGroup parent, LayoutInflater inflater);

    protected abstract void bindNormalView(View view, T item);

    protected View createHeaderView(Object section, ViewGroup parent, LayoutInflater inflater) {
        return inflater.inflate(R.layout.listview_section_header_item, parent, false);
    }

    protected void bindHeaderView(View view, Object section) {
        if (section != null) {
            TextView sectionTitle = (TextView) view.findViewById(R.id.section_title);
            if (sectionTitle != null) {
                if (section instanceof CharSequence) {
                    sectionTitle.setText((CharSequence) section);
                } else {
                    sectionTitle.setText(section.toString());
                }
            }
        }
    }

    /**
     * This provides the list view with an array of section objects. In the simplest case these are Strings, each
     * containing one letter of the alphabet. They could be more complex objects that indicate the grouping for the
     * adapter's consumption. The list view will call toString() on the objects to get the preview letter to display
     * while scrolling.
     * 
     * @return the array of objects that indicate the different sections of the list.
     */
    public Object[] getSections() {
        if (sectionIndexer != null) {
            return sectionIndexer.getSections();
        } else {
            return null;
        }
    }

    /**
     * Provides the starting index in the list for a given section.
     * 
     * @param section
     *            the index of the section to jump to.
     * @return the starting position of that section. If the section is out of bounds, the position must be clipped to
     *         fall within the size of the list.
     */
    public int getPositionForSection(int section) {
        if (sectionIndexer != null) {
            return sectionIndexer.getPositionForSection(section);
        } else {
            return -1;
        }
    }

    /**
     * This is a reverse mapping to fetch the section index for a given position in the list.
     * 
     * @param position
     *            the position for which to return the section
     * @return the section index. If the position is out of bounds, the section index must be clipped to fall within the
     *         size of the section array.
     */
    public int getSectionForPosition(int position) {
        if (sectionIndexer != null) {
            return sectionIndexer.getSectionForPosition(position);
        } else {
            return -1;
        }
    }

}
