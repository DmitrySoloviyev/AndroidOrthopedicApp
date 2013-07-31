package com.example.orthopedicdb;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends ArrayAdapter<String> {

	private final Activity context;
	private final String[] items;
	public MenuAdapter(Activity context, String[] TextValue) {
		super(context, R.layout.menu_simple_list_item, TextValue);
		this.context = context;
		this.items = TextValue;
	}

	// Класс для сохранения во внешний класс и для ограничения доступа из потомков класса
    static class ViewHolder {
    	public TextView textView;
        public ImageView imageView;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// ViewHolder буферизирует оценку различных полей шаблона элемента

        ViewHolder holder;
        // Очищает сущетсвующий шаблон, если параметр задан
        // Работает только если базовый шаблон для всех классов один и тот же
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.menu_simple_list_item, null, true);
            holder = new ViewHolder();
            holder.textView = (TextView) rowView.findViewById(R.id.menu_text);
            holder.imageView = (ImageView) rowView.findViewById(R.id.image_menu);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.textView.setText(items[position]);
        
        // Изменение иконки
/*		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.menu_simple_list_item, parent, false);

		TextView text = (TextView) rowView.findViewById(R.id.menu_text);
		text.setText(items[position]);
		ImageView img = (ImageView)rowView.findViewById(R.id.image_menu);	*/
		switch (position) {
		case 0:
			holder.imageView.setImageResource(android.R.drawable.ic_menu_add);
//			img.setImageResource(android.R.drawable.ic_menu_add);
			break;
		case 1:
			holder.imageView.setImageResource(android.R.drawable.ic_menu_search);
//			img.setImageResource(android.R.drawable.ic_menu_search);
			break;
		case 2:
			holder.imageView.setImageResource(android.R.drawable.ic_menu_sort_by_size);
//			img.setImageResource(android.R.drawable.ic_menu_view);
			break;
		case 3:
			holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery);
//			img.setImageResource(android.R.drawable.ic_menu_gallery);
			break;
		default:
			break;
		}
		return rowView;
	}

}
