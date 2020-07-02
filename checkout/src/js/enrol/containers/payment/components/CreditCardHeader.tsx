import React from "react";

interface Props {
  centered?: boolean;
}

const Header: React.StatelessComponent<Props> = ({centered}) =>
  <div className="header-content">
    <div className="header" style={centered ?
    {
      left: "50%",
      transform: "translateX(-50%)",
      display: "inline-block"
    } : null}>
      <h1>Secure credit card payment</h1>
      <span>This is a secure SSL encrypted payment.</span>
    </div>
  </div>;

export default Header;
