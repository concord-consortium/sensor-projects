// NGIO_MeasureView.h : interface of the CNGIO_MeasureView class
//
#pragma once

class CNGIO_MeasureView : public CView
{
protected: // create from serialization only
	CNGIO_MeasureView();
	DECLARE_DYNCREATE(CNGIO_MeasureView)

// Attributes
public:
	CNGIO_MeasureDoc* GetDocument() const;
	void SetGraphHistory(double y_min, double y_max)
	{
		graph_history_y_min = y_min;
		graph_history_y_max = y_max;
	}
	void GetGraphHistory(double &y_min, double &y_max)
	{
		y_min = graph_history_y_min;
		y_max = graph_history_y_max;
	}

// Operations
public:

// Overrides
public:
	virtual void OnDraw(CDC* pDC);  // overridden to draw this view
	virtual BOOL PreCreateWindow(CREATESTRUCT& cs);
protected:

// Implementation
public:
	virtual ~CNGIO_MeasureView();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:
//	void RecordGoIOString(LPCSTR pBuf, int buf_len, LPCSTR label);

// Generated message map functions
protected:
	afx_msg BOOL OnEraseBkgnd(CDC* pDC);
	DECLARE_MESSAGE_MAP()

	double graph_history_y_min;
	double graph_history_y_max;
};

#ifndef _DEBUG  // debug version in NGIO_MeasureView.cpp
inline CNGIO_MeasureDoc* CNGIO_MeasureView::GetDocument() const
   { return reinterpret_cast<CNGIO_MeasureDoc*>(m_pDocument); }
#endif

