import pytesseract
from pytesseract import Output
import cv2
import imutils
import numpy as np
from transform import four_point_transform

img = cv2.imread('../receipt-images/1017-receipt.jpg')

scale_factor = 500 if img.shape[0] >= 500 else img.shape[0]
ratio = img.shape[0] / scale_factor
orig = img.copy()
img = imutils.resize(img, height = scale_factor)


# convert the image to grayscale, blur it, and find edges
# in the image
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
gray = cv2.GaussianBlur(gray, (5, 5), 0)
# edged = cv2.Canny(gray, 75, 200)

th3 = cv2.adaptiveThreshold(gray,255,cv2.ADAPTIVE_THRESH_GAUSSIAN_C,\
            cv2.THRESH_BINARY,5,2)
thresh = cv2.threshold(th3, 0, 255, cv2.THRESH_BINARY | cv2.THRESH_OTSU)[1]
thresh = cv2.erode(thresh, None, iterations=2)
thresh = cv2.dilate(thresh, None, iterations=2)

cv2.imshow('th3',th3)
cv2.waitKey(0)
cv2.destroyAllWindows()

cnts, _ = cv2.findContours(thresh.copy(), cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
cnts = sorted(cnts, key=cv2.contourArea, reverse=True)[0:1]

# cv2.drawContours(img, cnts, -1, (0, 255, 255), 2)
# cv2.imshow('img',img)
# cv2.waitKey(0)
# cv2.destroyAllWindows()
for c in cnts:
	peri = cv2.arcLength(c, True)
	approx = cv2.approxPolyDP(c, 0.02 * peri, True)
	screenCnt = approx
cv2.drawContours(img, [screenCnt], -1, (0, 255, 255), 2)

warped = four_point_transform(orig, screenCnt.reshape(len(screenCnt), 2) * ratio)
warped_gray = cv2.cvtColor(warped, cv2.COLOR_BGR2GRAY)
# warped_gray = cv2.GaussianBlur(warped_gray, (3, 3), 0)
warped_gray = cv2.bilateralFilter(warped_gray,5,75,75)
warped_thresh = cv2.adaptiveThreshold(warped_gray,255,cv2.ADAPTIVE_THRESH_GAUSSIAN_C,\
            cv2.THRESH_BINARY,5,2)

# cv2.imshow('warped',warped)
cv2.imshow('warped_thresh',warped_thresh)
cv2.waitKey(0)
cv2.destroyAllWindows()
	

d = pytesseract.image_to_data(warped_thresh, output_type=Output.DICT)
n_boxes = len(d['level'])

for i in range(n_boxes):
	(x, y, w, h) = (d['left'][i], d['top'][i], d['width'][i], d['height'][i])
	warped_gray = cv2.rectangle(warped_gray, (x, y), (x + w, y + h), (0, 0, 255), 2)
cv2.imshow('with_rectangle',warped)
cv2.waitKey(0)
cv2.destroyAllWindows()

extracted_text = pytesseract.image_to_string(warped_thresh, lang = 'eng')
print(extracted_text)


