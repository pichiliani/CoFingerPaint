package com.ita.MyFingerPaint;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.*;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.util.DisplayMetrics;


public class MyFingerPaintActivity extends GraphicsActivity implements ColorPickerDialog.OnColorChangedListener {    

	private Paint       mPaint;
	private MaskFilter  mEmboss;
	private MaskFilter  mBlur;
	
	private static final int COLOR_MENU_ID = Menu.FIRST;
	private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
	private static final int BLUR_MENU_ID = Menu.FIRST + 2;
	private static final int ERASE_MENU_ID = Menu.FIRST + 3;
	private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;
	
	////////////////////////////////////////
	// COLAB
	///////////////////////////////////////
	private static int port = 0;
	private static String IP = "";
	private static String USER = "";
	private static String PASS = "";
	private static String SESS = "";
	private static View thisView;
	
	public String RECEBIDO_LOCAL = "0";
	
	
	/*** O objeto utilizado para envar os eventos   *     */
	public ClienteConecta clienteConecta = new ClienteConecta();
	
	/////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	
		MyView m =  new MyView(this);
		setContentView(m);
		
		
        ////////////////////////////////////////
        // COLAB
        ///////////////////////////////////////
		MyFingerPaintActivity.IP =	"192.168.1.101";
		MyFingerPaintActivity.port =	123;
		MyFingerPaintActivity.USER  =	"A";
		MyFingerPaintActivity.PASS  =	"A";
		MyFingerPaintActivity.SESS =	"sc1";
        
        thisView = m;
        ///////////////////////////////////////

		
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);
		
		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
		                               0.4f, 6, 3.5f);
		
		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
	}
	
	
	public void colorChanged(int color) 
	{
		mPaint.setColor(color);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
		menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
		menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('5', 'z');
		menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
		menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('5', 'z');
		
		menu.add(0, Menu.FIRST+5 , 0, "Settings").setShortcut('3', 'c');
		
		/****   Is this the mechanism to extend with filter effects?
		Intent intent = new Intent(null, getIntent().getData());
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		menu.addIntentOptions(
		                      Menu.ALTERNATIVE, 0,
		                      new ComponentName(this, NotesList.class),
		                      null, intent, 0, null);
		*****/
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) 
	{
		super.onPrepareOptionsMenu(menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);
		
		switch (item.getItemId()) 
		{
		    case COLOR_MENU_ID:
		    	
		        new ColorPickerDialog(this, this, mPaint.getColor()).show();
		        return true;
		    case EMBOSS_MENU_ID:
		        if (mPaint.getMaskFilter() != mEmboss) {
		            mPaint.setMaskFilter(mEmboss);
		        } else {
		            mPaint.setMaskFilter(null);
		        }
		        return true;
		    case BLUR_MENU_ID:
		        if (mPaint.getMaskFilter() != mBlur) {
		            mPaint.setMaskFilter(mBlur);
		        } else {
		            mPaint.setMaskFilter(null);
		        }
		        return true;
		    case ERASE_MENU_ID:
		        mPaint.setXfermode(new PorterDuffXfermode(
		                                                PorterDuff.Mode.CLEAR));
		        return true;
		    case SRCATOP_MENU_ID:
		        mPaint.setXfermode(new PorterDuffXfermode(
		                                            PorterDuff.Mode.SRC_ATOP));
		        mPaint.setAlpha(0x80);
		        return true;
		        
	   		case Menu.FIRST+5:
	    		this.openCollabDialog();
	   			return true;

		}
	
		return super.onOptionsItemSelected(item);
	}
	
   private void openCollabDialog() 
    {
	    	
	    	// This example shows how to add a custom layout to an AlertDialog
	        LayoutInflater factory = LayoutInflater.from(this);
	        
	        final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
	        
	        final EditText s = (EditText) textEntryView.findViewById(R.id.server_edit );
	        s.setText(MyFingerPaintActivity.IP);
	        
	        final EditText port = (EditText) textEntryView.findViewById(R.id.port_edit );
	        port.setText(  String.valueOf(MyFingerPaintActivity.port)  );
	        
	        
	        final EditText u = (EditText) textEntryView.findViewById(R.id.username_edit );
	        u.setText(MyFingerPaintActivity.USER);
	        
	        final EditText p = (EditText) textEntryView.findViewById(R.id.password_edit );
	        p.setText(MyFingerPaintActivity.PASS);
	        
	        final EditText sess = (EditText) textEntryView.findViewById(R.id.sess_edit);
	        sess.setText(MyFingerPaintActivity.SESS);
	        
	        
	        Dialog meuD = new AlertDialog.Builder(MyFingerPaintActivity.this)
	            .setIcon(R.drawable.icon)
	            .setTitle("Collaboration Settings")
	            .setView(textEntryView)
	            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) 
	                {

	                	
	                	// Toast.makeText() mostra uma mensagem de forma amigável!
	                	// Toast.makeText(HelloMauroActivity.this, s.getText(), Toast.LENGTH_SHORT).show();

	                	
	                	MyFingerPaintActivity.IP =	s.getText().toString();
	                	MyFingerPaintActivity.port =	Integer.valueOf(port.getText().toString());
	                    
	                	MyFingerPaintActivity.USER  =	u.getText().toString();
	                	MyFingerPaintActivity.PASS  =	p.getText().toString();
	                	MyFingerPaintActivity.SESS =	sess.getText().toString();

	                	
	                	MyFingerPaintActivity.this.InicializaConexao();
	                    
	                    Toast.makeText(MyFingerPaintActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
	                    
	                	
	                }
	            })
	            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {

	                    // User clicked cancel so do some stuff 
	                }
	            })
	            .create();
	         
	        meuD.show();
	          
    }
	   
   private void InicializaConexao()
   {
		
       //  System.out.println("Testando o console");
        
        // FAZENDO A CONEXÃO
		clienteConecta = new ClienteConecta();
		
		clienteConecta.SetaConecta(this.IP,this.port);
		
		if( clienteConecta.SetaUser(this.USER ,this.PASS))
		{
			clienteConecta.start();
		}
		
		// SETANDO A SEÇÃO
		
		if ( this.USER.equals("A"))
		{
		
			ArrayList l = new ArrayList();
	    	l.add(this.SESS); 
	    	l.add(null); // O MODELO DA NOVA SESSÃO
	    	
	    	// o terceiro elemento eh um arraylist contendo os Ids globais das Figs
	    	l.add(null);
	    	
			//  Enviando para o argo uma mensagem de 'protocolo'
			clienteConecta.EnviaEvento(l,"PROT_nova_sessao");
		}
		else
		{
			//  Enviando para o argo uma mensagem de 'protocolo'
			clienteConecta.EnviaEvento(this.SESS,"PROT_sessao_existente");
		}
		
	}
	    
    public static View getView()
	{
		return thisView;
	}
	
	public class MyView extends View 
	{
	
		private static final float MINP = 0.25f;
		private static final float MAXP = 0.75f;
		
		private Bitmap  mBitmap;
		private Canvas  mCanvas;
		private Path    mPath;
		
		
		////////////////////////////////////////
		// COLAB
		///////////////////////////////////////
		// o Objeto handler é criado para poder mexer na thread de UI
		public Handler handler;
		
		public Path    mPath_remote;
		private float mX_remote, mY_remote;
		///////////////////////////////////////
		
		private Paint   mBitmapPaint;
		
		private float mX, mY;
		private static final float TOUCH_TOLERANCE = 4;

	
		public MyView(Context c) 
		{
		    super(c);
		    
		    handler = new Handler();
		    
		    DisplayMetrics dm = new DisplayMetrics();
		    getWindowManager().getDefaultDisplay().getMetrics(dm);
		    
		    
		    mBitmap = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_8888);
		    mCanvas = new Canvas(mBitmap);
		    mPath = new Path();
		    
			////////////////////////////////////////
			// COLAB
			///////////////////////////////////////
		    mPath_remote = new Path();
		    
		    
		    mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		}
	
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) 
		{
		    super.onSizeChanged(w, h, oldw, oldh);
		}
	
		@Override
		protected void onDraw(Canvas canvas) 
		{
		    canvas.drawColor(0xFFAAAAAA);
		    
		    canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		    
		    canvas.drawPath(mPath, mPaint);
		    canvas.drawPath(mPath_remote, mPaint);
		}
		
	
		private void touch_start(float x, float y) 
		{
		    mPath.reset();
		    mPath.moveTo(x, y);
		    mX = x;
		    mY = y;
		    
			////////////////////////////////////////
			// COLAB
			///////////////////////////////////////
	    	// Enviando os dados
	    	ArrayList l = new ArrayList();
			l.add("MyView");  // O nome completamente qualificiado do widget (View_controle) 
			l.add("Path");                  // O tipo do controle
			l.add("");                         // A operação 
			l.add(x);  				// Pos x
			l.add(y);              // Pos y                        
			    
	        clienteConecta.EnviaEvento(l,"TOUCH_START");
	        /////////////////////////////////////

		    
		}
		
		////////////////////////////////////////
		// COLAB
		///////////////////////////////////////
		public void touch_startImpl(float x, float y) 
		{
			mPath_remote.reset();
			mPath_remote.moveTo(x, y);
		    mX_remote = x;
		    mY_remote = y;
		    
		    invalidate();
		}

		private void touch_move(float x, float y) 
		{
		    float dx = Math.abs(x - mX);
		    float dy = Math.abs(y - mY);
		    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) 
		    {
		        mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
		        
				////////////////////////////////////////
				// COLAB
				///////////////////////////////////////
		    	// Enviando os dados
		    	ArrayList l = new ArrayList();
				l.add("MyView");  // O nome completamente qualificiado do widget (View_controle) 
				l.add("Path");                  // O tipo do controle
				l.add("");                         // A operação 
				l.add(x);  				// Pos x
				l.add(y);               //  Pos y
				    
		        clienteConecta.EnviaEvento(l,"TOUCH_MOVE");
		        /////////////////////////////////////
		        
		        mX = x;
		        mY = y;
		        
		    }
		}
		
		////////////////////////////////////////
		// COLAB
		///////////////////////////////////////
		public void touch_moveImpl(float x, float y) 
		{
		    float dx = Math.abs(x - mX_remote);
		    float dy = Math.abs(y - mY_remote);
		    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) 
		    {
		    	mPath_remote.quadTo(mX_remote, mY_remote, (x + mX_remote)/2, (y + mY_remote)/2);
		        
		        mX_remote = x;
		        mY_remote = y;
		        
		    }
		    
		    invalidate();
		}
		
		private void touch_up() 
		{
		    mPath.lineTo(mX, mY);
		    // commit the path to our offscreen
		    mCanvas.drawPath(mPath, mPaint);
		    // kill this so we don't double draw
		    mPath.reset();
		    
			////////////////////////////////////////
			// COLAB
			///////////////////////////////////////
	    	// Enviando os dados
	    	ArrayList l = new ArrayList();
			l.add("MyView");  // O nome completamente qualificiado do widget (View_controle) 
			l.add("Path");                  // O tipo do controle
			l.add("");                         // A operação 
			    
	        clienteConecta.EnviaEvento(l,"TOUCH_UP");
	        /////////////////////////////////////
		}
		
		////////////////////////////////////////
		// COLAB
		///////////////////////////////////////
		public void touch_upImpl() 
		{
			mPath_remote.lineTo(mX_remote, mY_remote);
		    // commit the path to our offscreen
		    mCanvas.drawPath(mPath_remote, mPaint);
		    // kill this so we don't double draw
		    mPath_remote.reset();
		    
		    invalidate();
		    
		}
	
		@Override
		public boolean onTouchEvent(MotionEvent event) 
		{
		    float x = event.getX();
		    float y = event.getY();
		    
		    switch (event.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		            touch_start(x, y);
		            invalidate();
		            break;
		        case MotionEvent.ACTION_MOVE:
		            touch_move(x, y);
		            invalidate();
		            break;
		        case MotionEvent.ACTION_UP:
		            touch_up();
		            invalidate();
		            break;
		    }
		    return true;
		}
	}
	
}
