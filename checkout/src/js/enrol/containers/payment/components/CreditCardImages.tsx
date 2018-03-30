import React from "react";

const amexImgSrc = "/s/img/amex.png";
const visaMasterCardImgSrc = "/s/img/visa-mastercard.png";
const cvvImgSrc = "s/img/cvv-image.png";

export const VisaMasterCardImg = () => (
  <img src={visaMasterCardImgSrc} alt="visa card and master card" />
);

export const AmexImg = () => (
  <img src={amexImgSrc} alt="amex"/>
);

export const CvvImg = () => (
  <img className="vcc-card-image" src={cvvImgSrc} alt="CVV"/>
);

