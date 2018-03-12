package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.app.bean.OneLevelChooseData;
import com.qianniu.zhaopin.app.bean.TwoLevelChooseData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TwoLevelChooseListAdapter extends BaseExpandableListAdapter {
	public static final int TYPE_INDUSTRY = 0;			// 行业选择
	public static final int TYPE_JOBFUNCTION = 1;		// 职能分类选择

    private Context m_Context; 
    private LayoutInflater m_infater = null;
     
    private List<TwoLevelChooseData> m_lsICD;
    
    private int m_nType;
    
    private boolean m_bIsGetHeight = false;			// 是否已经获取到父行业控件的高度 
    private int m_nHeight;
    
    private IndustryParentViewHolder m_Parentholder;
    
    public TwoLevelChooseListAdapter(Context _context, List<TwoLevelChooseData> list, int nType){   	
    	this.m_Context = _context;
    	this.m_infater = (LayoutInflater) m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	this.m_lsICD = list;
    	this.m_nHeight = 0;
    	this.m_nType = nType;
    }

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		TwoLevelChooseData icd = m_lsICD.get(groupPosition);
		return icd.getSubList().get(childPosition);
	}
	
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		IndustryChildViewHolder viewHolder = null;
		if(convertView == null){
			convertView = m_infater.inflate(R.layout.list_item_childindustry, null);
			viewHolder = new IndustryChildViewHolder(convertView);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (IndustryChildViewHolder)convertView.getTag();
		}
	
		TwoLevelChooseData icd = m_lsICD.get(groupPosition);

		OneLevelChooseData icsd = icd.getSubList().get(childPosition);
		
		viewHolder.tv_ChildIndustry.setText(icsd.getName());
        
        // 选择图标
		if(icsd.getIsChoose()){
			viewHolder.img_ISChoose.setVisibility(View.VISIBLE);
//        	viewHolder.img_ISChoose.setImageResource(R.drawable.common_img_check_green);
        }else{
        	if(TYPE_JOBFUNCTION == m_nType){
        		viewHolder.img_ISChoose.setImageResource(R.drawable.common_img_next_big);
        	}else{
            	viewHolder.img_ISChoose.setVisibility(View.INVISIBLE);
//            	viewHolder.img_ISChoose.setImageResource(R.drawable.common_img_check_gray);       		
        	}
        }
		
		return convertView;
	}
	
	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		TwoLevelChooseData icd = m_lsICD.get(groupPosition);
		return icd.getSubList().size();
	}
	
	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return m_lsICD.get(groupPosition);
	}
	
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return m_lsICD.size();
	}
	
	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		m_Parentholder = null;
		if(convertView == null){
			convertView = m_infater.inflate(R.layout.list_item_parentindustry, null);
			m_Parentholder = new IndustryParentViewHolder(convertView);
			convertView.setTag(m_Parentholder);
		}else{
			m_Parentholder = (IndustryParentViewHolder)convertView.getTag();
		}
		
		// 设置父类名
		m_Parentholder.tv_ParentIndustry.setText(m_lsICD.get(groupPosition).getName());
        
        // 判断isExpanded就可以控制是按下还是关闭，同时更换图片
        if(isExpanded){
        	m_Parentholder.img_ParentOpen.setImageResource(R.drawable.common_img_up);
        }else{
        	m_Parentholder.img_ParentOpen.setImageResource(R.drawable.common_img_down);
        }
        
        // 类型图标
        if(TYPE_JOBFUNCTION != m_nType){
            if(m_lsICD.get(groupPosition).getHavingColor()){
                if(m_bIsGetHeight){
        			Bitmap bmp = createCategoryBmp(m_nHeight, 
        					m_lsICD.get(groupPosition).getID());
        			m_Parentholder.img_ParentType.setImageBitmap(bmp);	
                }else{
        			ViewTreeObserver vto = m_Parentholder.ll_ParentIndustry.getViewTreeObserver();
        			vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){
        				@Override
        				public boolean onPreDraw() {
        					// TODO Auto-generated method stub
        					if(!m_bIsGetHeight){
        						m_nHeight = m_Parentholder.ll_ParentIndustry.getMeasuredHeight();
        						m_bIsGetHeight = true;
        						
        						notifyDataSetChanged();
        					}
        					return true;
        				}
        			});	
                }        	
            }        	
        }
        
        return convertView;
	}
	
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
    
	static class IndustryParentViewHolder{
		TextView tv_ParentIndustry;		// 父行业名 
		
		ImageView img_ParentType;		// 父行类型颜色
		ImageView img_ParentOpen;		// 子行业展开/收起图标		
		
		LinearLayout ll_ParentIndustry;		// 
		
		public IndustryParentViewHolder(View view){
			this.tv_ParentIndustry = (TextView) view.findViewById(R.id.list_item_industrychoose_tv_parentname);

	        this.img_ParentOpen  = (ImageView) view.findViewById(R.id.list_item_industrychoose_img_open);
	        this.img_ParentType = (ImageView) view.findViewById(R.id.list_item_industrychoose_img_industry);
	        
        	this.ll_ParentIndustry = (LinearLayout)view.findViewById(R.id.list_item_industrychoose_ll_parent);
		}
	}
    
	static class IndustryChildViewHolder{
        TextView tv_ChildIndustry;			// 子行业名 
        ImageView img_ISChoose;				// 被选中图标
        
        LinearLayout ll_ChildIndustry;		// 
        
        public IndustryChildViewHolder(View view){
        	this.tv_ChildIndustry = (TextView) view.findViewById(R.id.list_item_subindustry_name);
        	this.img_ISChoose = (ImageView) view.findViewById(R.id.list_item_subindustry_img_check);
        	this.ll_ChildIndustry = (LinearLayout)view.findViewById(R.id.list_item_subindustry_ll_subindustry);
        }
	}
	
	/**
	 * 绘制类型图
	 * @param nHeight
	 * @param strID
	 * @return
	 */
	private Bitmap createCategoryBmp(int nHeight, String strID){
		int nW = 5;
		
		// 紫色
		int nColor = Color.argb(127, 920, 40, 124);
		if(null != strID){
			int nType = Integer.valueOf(strID);
			switch(nType){
			case HeadhunterPublic.INDUSTRY_PARENTDATAID_TECHNOLOGY:
				{
					// 蓝色
					nColor = Color.argb(127, 43, 147, 252);
				}
				break;
			case HeadhunterPublic.INDUSTRY_PARENTDATAID_ADVERTISINGCOMPANY:
				{
					// 大红
					nColor = Color.argb(127, 193, 26, 17);
				}
				break;
			case HeadhunterPublic.INDUSTRY_PARENTDATAID_MEDIA:
				{
					// 绿色
					nColor = Color.argb(127, 116, 194, 86);
				}
				break;
			case HeadhunterPublic.INDUSTRY_PARENTDATAID_BRAND:
				{
					// 橘色
					nColor = Color.argb(127, 232, 117, 5);
				}
				break;
			case HeadhunterPublic.INDUSTRY_PARENTDATAID_OHTER:
			default:
				{
					// 紫色
					nColor = Color.argb(127, 920, 40, 124);
				}
				break;
			}
		}

		Bitmap bmpRes = Bitmap.createBitmap(nW, nHeight, Config.ARGB_8888);
		
		Canvas canvasTemp = new Canvas(bmpRes);
		canvasTemp.drawColor(nColor);
		//canvasTemp.drawColor(Color.argb(0x55, 0xff, 0, 0));    

		return bmpRes;
	}
}
