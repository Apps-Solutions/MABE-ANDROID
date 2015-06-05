package util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sellcom.tracker.FragmentClients;
import com.sellcom.tracker.R;

import java.util.List;

import database.models.Customer;

/**
 * Created by juanc.jimenez on 01/08/14.
 */
public class ClientsAdapter extends BaseAdapter implements View.OnClickListener{

    Context context;
    List<Customer> clients;
    OnClientDeletedListener onClientDeletedListener;

    public ClientsAdapter(Context context, List<Customer> clients) {

        this.context = context;
        this.clients = clients;
    }

    @Override
    public int getCount() {
        return clients.size();
    }

    @Override
    public Object getItem(int i) {
        return clients.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        final ViewHolder viewHolder;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_clients, viewGroup, false);

            viewHolder = new ViewHolder();

            viewHolder.deleteButton = (ImageButton) view.findViewById(R.id.delete_button);
            viewHolder.clientName = (TextView) view.findViewById(R.id.client_name);
            viewHolder.clientPhone = (TextView) view.findViewById(R.id.client_phone);
            viewHolder.clientEmail = (TextView) view.findViewById(R.id.client_email);

            viewHolder.deleteButton.setOnClickListener(this);

            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();


        Customer item = clients.get(position);

        viewHolder.clientName.setText(item.getInfo().getFirstName());
        viewHolder.clientPhone.setText(item.getInfo().getPhone());
        viewHolder.clientEmail.setText(item.getInfo().getEmail());

        if (FragmentClients.hideButtons)
            viewHolder.deleteButton.setVisibility(View.GONE);
        else
            viewHolder.deleteButton.setVisibility(View.VISIBLE);

        viewHolder.deleteButton.setTag(position);
        viewHolder.position = position;

        return view;
    }

    @Override
    public void onClick(View view) {

        int position = (Integer)view.getTag();
        Customer.delete(context, clients.get(position).getId());
        clients.remove(position);
        notifyDataSetChanged();

        onClientDeletedListener.onClientDeleted(position);
    }

    public void filterProducts(String query) {

        query = query.toLowerCase();

        List<Customer> clientsCopy = Customer.getAll(context);
        clients.clear();

        if (query.isEmpty()) {
            clients.addAll(clientsCopy);
        } else {
            for (Customer item : clientsCopy)
            {
                if (item.getInfo().getFullName().toLowerCase().contains(query)
                        || item.getInfo().getEmail().toLowerCase().contains(query)
                        || item.getInfo().getPhone().contains(query)) {
                    if (!clients.contains(item))
                        clients.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface OnClientDeletedListener{

        public void onClientDeleted(int position);
    }

    public void setOnClientDeletedListener(OnClientDeletedListener listener) {

        onClientDeletedListener = listener;
    }

    class ViewHolder{

        ImageButton deleteButton;
        TextView clientName;
        TextView clientPhone;
        TextView clientEmail;

        int position;
    }
}
