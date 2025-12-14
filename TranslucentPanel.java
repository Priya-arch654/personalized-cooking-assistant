package com.project;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;


	class TranslucentPanel extends JPanel {
        private Color color;
        private int arc;
        public TranslucentPanel(Color color, int arc) {
            this.color = color;
            this.arc = arc;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(color);
            g2.fill(new RoundRectangle2D.Float(0,0,w,h,arc,arc));
            g2.dispose();
            super.paintComponent(g);
        }
    }

