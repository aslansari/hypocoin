package com.aslansari.hypocoin.ui.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // region Constants
    protected static final int HEADER = 0;
    protected static final int ITEM = 1;
    protected static final int FOOTER = 2;
    // endregion

    // region Member Variables
    protected List<T> items;
    protected OnItemClickListener onItemClickListener;
    protected OnReloadClickListener onReloadClickListener;
    protected OnLongClickListener onLongClickListener;
    protected boolean isFooterAdded = false;
    // endregion

    // region Constructors
    public BaseAdapter() {
        items = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case HEADER:
                viewHolder = createHeaderViewHolder(parent);
                break;
            case ITEM:
                viewHolder = createItemViewHolder(parent);
                break;
            case FOOTER:
                viewHolder = createFooterViewHolder(parent);
                break;
            default:
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case HEADER:
                bindHeaderViewHolder(viewHolder);
                break;
            case ITEM:
                bindItemViewHolder(viewHolder, position);
                break;
            case FOOTER:
                bindFooterViewHolder(viewHolder);
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    // endregion

    // region Abstract Methods
    protected abstract RecyclerView.ViewHolder createHeaderViewHolder(ViewGroup parent);
    // endregion

    protected abstract RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent);

    protected abstract RecyclerView.ViewHolder createFooterViewHolder(ViewGroup parent);

    protected abstract void bindHeaderViewHolder(RecyclerView.ViewHolder viewHolder);

    protected abstract void bindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position);

    protected abstract void bindFooterViewHolder(RecyclerView.ViewHolder viewHolder);

    protected abstract void displayLoadMoreFooter();

    protected abstract void displayErrorFooter();

    public abstract void addFooter();

    // region Helper Methods
    public T getItem(int position) {
        return items.get(position);
    }

    public void add(T item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void add(int position, T item) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void addAll(List<T> items) {
        for (T item : items) {
            add(item);
        }
    }
    // endregion

    public void addAll(int position, List<T> items) {
        for (T item : items) {
            add(position, item);
        }
    }

    public void remove(T item) {
        int position = items.indexOf(item);
        if (position > -1) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        Timber.d("clear recyclerView");
        isFooterAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public boolean isLastPosition(int position) {
        return (position == items.size() - 1);
    }

    public boolean isFooterAdded() {
        return isFooterAdded;
    }

    public void removeFooter() {
        isFooterAdded = false;

        int position = items.size() - 1;
        T item = getItem(position);

        if (item != null) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateFooter(FooterType footerType) {
        switch (footerType) {
            case LOAD_MORE:
                displayLoadMoreFooter();
                break;
            case ERROR:
                displayErrorFooter();
                break;
            default:
                break;
        }
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnReloadClickListener(OnReloadClickListener onReloadClickListener) {
        this.onReloadClickListener = onReloadClickListener;
    }

    // region Inner Classes
    public enum FooterType {
        LOAD_MORE,
        ERROR
    }

    // region Interfaces
    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }

    public interface OnReloadClickListener {
        void onReloadClick();
    }

    public interface OnLongClickListener {
        void onItemLongClick(int position, View view);
    }
    // endregion

    public interface ISortable {
        void sort();
    }
    // endregion
}
