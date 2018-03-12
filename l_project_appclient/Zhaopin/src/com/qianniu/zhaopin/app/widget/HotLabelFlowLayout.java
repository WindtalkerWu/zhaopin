package com.qianniu.zhaopin.app.widget;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.bean.OneLevelData;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.ui.BaseActivity;
import com.qianniu.zhaopin.app.ui.HotLabelActivity;
import com.qianniu.zhaopin.app.ui.RewardSearchActivity;
import com.qianniu.zhaopin.util.NumberUtils;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HotLabelFlowLayout extends LabelFlowLayout
{
    private final static String TAG = "HotLabelFlowLayout";
    private List<OneLevelData> selectedLabels;
    private List<OneLevelData> labels;
    private int flag;


    public static int HOTLABEL = 1;
    public static int ALLLABEL = 2;
    public static int PARTLABEL = 3;
    
    public static int hotCount = 30;
    public static int partCount = 5;

    public static int MAXSELECTEDSIZE = 5;
    
    private int[] random;
    
    public HotLabelFlowLayout(Context context) {
        super(context);
        selectedLabels = new ArrayList<OneLevelData>();
        initViewMargin();
    }
    
    public HotLabelFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public HotLabelFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        selectedLabels = new ArrayList<OneLevelData>();
        setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
    }
    public List<OneLevelData> getSelectedLabels() {
		return selectedLabels;
	}
    public void setSelectedLabels(List<OneLevelData> selectedLabels) {
		this.selectedLabels = selectedLabels;
	}
    public void addSelected(OneLevelData label) {
    	selectedLabels.add(label);
    	refreshLabels();
    }
    public void addSelectedNew(OneLevelData label) {
    	selectedLabels.add(label);
    	refreshLabels();
    }
    public void removeSelected(OneLevelData label) {
    	if (getSelectedCount() <= 0) {
    		return;
    	}
    	for (OneLevelData oneLevelData : selectedLabels) {
			if (oneLevelData.getLabel().equals(label.getLabel())) {
		    	selectedLabels.remove(oneLevelData);
		    	break;
			}
		}
    	refreshLabels();
    }
    public int getSelectedCount() {
    	return selectedLabels.size();
    }
    public void clearSelectedLables() {
    	selectedLabels.clear();
    }
    public void refreshLabels() {
    	if (activity instanceof RewardSearchActivity) {
        	((RewardSearchActivity)activity).changeLabels(selectedLabels);
    	}
    	if (activity instanceof HotLabelActivity) {
        	((HotLabelActivity)activity).changeLabels(selectedLabels);
    	}
    }
    public void refreshLabel(OneLevelData label) {
    	if (activity instanceof RewardSearchActivity) {
        	((RewardSearchActivity)activity).changeLabel(label);
    	}
    }
    public void resetLabels() {
    	refreshLabels();
    	removeAllViews();
    	initData(activity, flag, false);
    }
    public void startRewardSearchLabelActivity() {
    	if (activity instanceof RewardSearchActivity) {
        	((RewardSearchActivity)activity).startRewardSearchLabelActivity();
    	}
    }

    public void initData(BaseActivity activity, int flag, boolean isFreash) {// 0 all 10 前10个  30 前30个
    	this.activity = activity;
    	this.flag = flag;
		getDataFromDB();
		if (labels == null) {
			return;
		}
		int max = 0;
		int n = 0;
//		HashSet<Integer> hashSet = new HashSet<Integer>();
		int length = labels.size();
		if (flag == ALLLABEL) {

			for (int i = 0; i < length; i++) {
				HotLabelFlowItem labelFlowItem = new HotLabelFlowItem(getContext(), this);
				int imageId = i % UIHelper.labelFlowImages.length;
				if (selectedLabels != null && selectedLabels.size() > 0) {
					for (OneLevelData selectedLabel : selectedLabels) {
						if (selectedLabel.getLabel().equals(labels.get(i).getLabel())) {
							labelFlowItem.setSelected(true);
							break;
						}
					}
				}
				labelFlowItem.initData(labels.get(i), UIHelper.labelFlowImages[imageId], flag);
				addView(labelFlowItem);
			}
		} else {
			if (isFreash) {
				if (flag == PARTLABEL) {
					max = length - 1;
					n = partCount;
					if (length < partCount) {
						n = length;
					}
					random = NumberUtils.rang(0, max, n);
				} else if (flag == HOTLABEL) {
					max = length - 1;
					n = hotCount;
					if (length < hotCount) {
						n = length;
					}
					random = NumberUtils.rang(0, max, n);
				}
			}
//			Object[] random = (Object[]) hashSet.toArray(); //将set转换成数组
			for (int i = 0; i < random.length; i++) {
				HotLabelFlowItem labelFlowItem = new HotLabelFlowItem(getContext(), this);
				int imageId = i % UIHelper.labelFlowImages.length;
				if (selectedLabels != null && selectedLabels.size() > 0) {
					for (OneLevelData selectedLabel : selectedLabels) {
						if (selectedLabel.getLabel().equals(labels.get(random[i]).getLabel())) {
//						if (selectedLabel.getId()== labels.get(random[i]).getId()) {
							labelFlowItem.setSelected(true);
							break;
						}
					}
				}
				labelFlowItem.initData(labels.get(random[i]), UIHelper.labelFlowImages[imageId], flag);
				addView(labelFlowItem);
			}
		}
		
//		if (flag == PARTLABEL) {
//			LabelFlowItem addButton = new LabelFlowItem(getContext(),this);
//			addButton.initData(null, R.drawable.add_button);
//			addButton.setWidth(65);
//			addView(addButton);
//		}
	}

	private void getDataFromDB() {
		DBUtils dbUtils = DBUtils.getInstance(getContext());
		String sql = "select * from " + DBUtils.globaldataTableName
				+ " where " + DBUtils.KEY_GLOBALDATA_TYPE + "=" + DBUtils.GLOBALDATA_TYPE_HOTKEYWORD;
//		Cursor cursor = dbUtils.query(true, DBUtils.globaldataTableName, new String[]{DBUtils.KEY_GLOBALDATA_NAME},
//				DBUtils.KEY_GLOBALDATA_TYPE, new String[]{13 + ""}, null, null, null, null);
		Cursor cursor = dbUtils.rawQuery(sql, null);
		if (cursor != null){
			if(cursor.getCount() > 0) {
				labels = new ArrayList<OneLevelData>();
				cursor.moveToFirst();
				do {
					OneLevelData oneLevelData = new OneLevelData();
					oneLevelData.setId(cursor.getString(cursor.getColumnIndex(DBUtils.KEY_GLOBALDATA_ID)));
					oneLevelData.setLabel(cursor.getString(cursor.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAME)));
					oneLevelData.setPinYin(cursor.getString(cursor.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAMEPINYIN)));
					labels.add(oneLevelData);
				} while (cursor.moveToNext());
			}
			
			cursor.close();
		}
	}
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	super.onLayout(changed, l, t, r, b);
    }
}
