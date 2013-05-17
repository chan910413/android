package com.lenovo_rel.app.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lenovo_rel.app.App;
import com.lenovo_rel.app.R;

import connection.Ajax;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ViewFlipper;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import custom.BaseActivity;

public class FeedBackActivity extends BaseActivity {
	
	private float oldTouchValue;
	
	private ViewFlipper feedback_vf;
	
	private RadioGroup feedback_gp_one;
	private RadioGroup feedback_gp_two;
	
	private RadioButton feedback_rb_one;
	private RadioButton feedback_rb_two;
	private RadioButton feedback_rb_six;
	
	
	private Button feedback_bt_submit;
	
	private String[] values = new String[2];

	@Override
	protected int getLayoutResourceId() {
		return R.layout.activity_feedback;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		initView();
		getSurvey();
		feedback_gp_one.setOnCheckedChangeListener(new MyOnCheckedChangeListener(0));
		feedback_gp_two.setOnCheckedChangeListener(new MyOnCheckedChangeListener(1));
		feedback_bt_submit.setOnClickListener(new MyOnClickListener());
	}

	
	private void getSurvey() {
		new Ajax(this, "/data/getSurvey", "获取数据中，，"){
			
			protected void onSuccess(org.json.JSONObject jObj) throws org.json.JSONException {
				JSONObject addon = jObj.getJSONObject("addon");
				JSONObject content = addon.getJSONObject("content");
				JSONArray questions = content.getJSONArray("question");
				for (int i = 0;i<questions.length();i++) {
					JSONObject question = (JSONObject) questions.get(i);
					Integer question_id = question.getInt("question_id");
					String question_title = question.getString("question_title");
					Integer question_type = question.getInt("question_type");
					JSONArray options = question.getJSONArray("option");
					for (int j = 0; j < options.length(); j++) {
						JSONObject option = (JSONObject) options.get(j);
						Integer option_id = option.getInt("option_id");
						String option_title = option.getString("option_title");
						Integer option_type = option.getInt("option_type");
						System.out.println("------------------");
						System.out.println(option_title);
					}
				}
			};
		}.addParam("survey_id", "1")
		.start();
		
	}

	private void initView() {
		feedback_gp_one = (RadioGroup) findViewById(R.id.feedback_gp_one);
		feedback_gp_two = (RadioGroup) findViewById(R.id.feedback_gp_two);
		feedback_rb_one = (RadioButton) findViewById(R.id.feedback_rb_one);
		feedback_rb_two = (RadioButton) findViewById(R.id.feedback_rb_two);
		feedback_rb_six = (RadioButton)findViewById(R.id.feedback_rb_six);
		feedback_bt_submit = (Button) findViewById(R.id.feedback_bt_submit);
		
		feedback_vf = (ViewFlipper) this.findViewById(R.id.feedback_vf);
		
	}
	
	private class MyOnCheckedChangeListener implements OnCheckedChangeListener{
		
		private Integer position;
		
		public MyOnCheckedChangeListener(Integer position) {
			this.position = position;
		}
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == feedback_rb_one.getId()) {
				values[position] = feedback_rb_one.getText().toString();
            } else if (checkedId == feedback_rb_two.getId()) {
            	values[position] = feedback_rb_two.getText().toString();
            }else if (checkedId == feedback_rb_six.getId()) {
            	values[position] = feedback_rb_six.getText().toString();
            }
		}


		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent touchevent) {

		switch (touchevent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                oldTouchValue = touchevent.getX();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                //if(this.searchOk==false) return false;
                float currentX = touchevent.getX();
                if (oldTouchValue < currentX)
                {
                    //vf.setInAnimation(null);
                	AnimationSet out = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.right_out);
                	AnimationSet in = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.right_in);
                    feedback_vf.setInAnimation(in);
                    feedback_vf.setOutAnimation(out);
                    feedback_vf.showPrevious();
                }
                if (oldTouchValue > currentX)
                {
                   // vf.setInAnimation(AnimationHelper.inFromRightAnimation());
                	AnimationSet out = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.left_out);
                	AnimationSet in = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.left_in);
                	feedback_vf.setOutAnimation(out);
                	feedback_vf.setInAnimation(in);
                	feedback_vf.showNext();
                }
            break;
            }
        }
		return true;
	}
	
	private class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.feedback_bt_submit:
				Toast.makeText(FeedBackActivity.this, values[0], Toast.LENGTH_SHORT).show();
				Toast.makeText(FeedBackActivity.this, values[1], Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		App app = (App) getApplication();
		app.activities.add(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		App app = (App) getApplication();
		app.activities.remove(this);
	}
}
