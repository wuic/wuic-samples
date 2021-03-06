/*
 * Copyright (c) 2013  Capgemini Technology Services (hereinafter “Capgemini”)
 *
 * License/Terms of Use
 *
 * Permission is hereby granted, free of charge and for the term of intellectual property rights on the Software, to any
 * person obtaining a copy of this software and associated documentation files (the "Software"), to use, copy, modify
 * and propagate free of charge, anywhere in the world, all or part of the Software subject to the following mandatory conditions:
 *
 *   •    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  Any failure to comply with the above shall automatically terminate the license and be construed as a breach of these
 *  Terms of Use causing significant harm to Capgemini.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 *  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 *  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  Except as contained in this notice, the name of Capgemini shall not be used in advertising or otherwise to promote
 *  the use or other dealings in this Software without prior written authorization from Capgemini.
 *
 *  These Terms of Use are subject to French law.
 */

/**
 * A CGSGNodeCircle represent a basic circle.
 * By default, the pickNodeMethod used to detect the node under the mice is CGSGPickNodeMethod.GHOST.
 * If you don't need precision on detection on your circles, just change the property to pickNodeMethod.REGION.
 *
 * @class CGSGNodeCircle
 * @module Node
 * @extends CGSGNode
 * @constructor
 * @param {Number} centerX Relative position
 * @param {Number} centerY Relative position
 * @param {Number} radius Radius
 * @type {CGSGNodeCircle}
 * @author xymostech (Emily Eisenberg)
 */
var CGSGNodeCircle = CGSGNode.extend(
    {
        initialize: function (centerX, centerY, radius) {

            this.radius = radius;
            this._center = new CGSGPosition(centerX, centerY);

            this._super(0, 0);
            this.resizeTo(radius * 2.0, radius * 2.0);

            this.isProportionalResizeOnly = true;

            this.translateTo(centerX - radius, centerY - radius);

            this.color = "#444444";
            this.lineColor = "#222222";
            this.lineWidth = 0;
        },

        render: function (context) {
            context.beginPath();
            context.arc(this.radius, this.radius, this.radius, 0, 2 * Math.PI, false);
            context.fillStyle = this.color;
            context.fill();
            if (this.lineWidth > 0) {
                context.lineWidth = this.lineWidth;
                context.strokeStyle = this.lineColor;
                context.stroke();
            }
        },

        /**
         * @method _resize
         * @private
         */
        _resize: function () {
            this.radius = CGSGMath.fixedPoint(this.dimension.width / 2);
            this._isDimensionChanged = true;
            this.invalidate();
            if (cgsgExist(this.onResize)) {
                CGSG.eventManager.dispatch(this, cgsgEventTypes.ON_RESIZE, new CGSGEvent(this, {node: this}));
            }
        },

        resizeTo: function (w, h) {
            var r = Math.min(w, h);
            this.dimension.resizeTo(r, r);
            this._resize();
        },

        resizeBy: function (w, h) {
            var mw = w * this.dimension.width;
            var mh = h * this.dimension.height;
            var r = (mw < mh) ? w : h;
            this.dimension.resizeBy(r, r);
            this._resize();
        },

        resizeWith: function (w, h) {
            var mw = w + this.dimension.width;
            var mh = h + this.dimension.height;
            var r = (mw < mh) ? w : h;
            this.dimension.resizeWith(r, r);
            this._resize();
        },

        /**
         * @method copy
         * @return {CGSGNodeCircle} a copy of this node
         */
        copy: function () {
            var node = new CGSGNodeCircle(this.position.x, this.position.y, this.radius);
            //call the super method
            node = this._super(node);

            node.color = this.color;
            node.lineColor = this.lineColor;
            node.lineWidth = this.lineWidth;
            return node;
        }
    }
);
