/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import React from "react";

const CreditsItem = ({ heading, creditPersons, className }) => (<div className="mb-3">
  <Typography variant="body1" className={className}>
    {heading}
  </Typography>
  {creditPersons.map((person, i) => (
    <Typography key={i} variant="body2">
      {person}
    </Typography>
  ))}
</div>);

const Credits = ({ wrapperClass, itemClass }) => {
  return <Grid container columnSpacing={3} className={wrapperClass}>
    <Grid item xs={12} sm={6}>
      <div>
        <CreditsItem heading="Product design" creditPersons={["Aristedes Maniatis", "Natalie Morton", "James Swinbanks"]} className={itemClass}  />
        <CreditsItem heading="System architecture" creditPersons={["Aristedes Maniatis"]} className={itemClass}  />
        <CreditsItem heading="Engineering leads" creditPersons={[
          "Yury Yasuchenya",
          "Artyom Kravchenko",
          "Andrey Koyro",
          "Anton Sakalouski",
          "Lachlan Deck",
          "Marek Wawrzyczny"
        ]} className={itemClass}  
        />
        <CreditsItem heading="Software engineering" creditPersons={[
          "Dmitry Tarasenko",
          "Kristina Trukhan",
          "Chintan Kotadia",
          "Dmitry Litvinko",
          "Victor Yarmolovich",
          "Vadim Haponov",
          "Rostislav Zenov",
          "Andrey Davidovich",
          "Pavel Nikanovich",
          "Artyom Kochetkov",
          "Alexandr Petkov",
          "Maxim Petrusevich",
          "Rostislav Zenov",
          "Arseni Bulatski",
          "Nikita Timofeev",
          "Marcin Skladaniec",
          "Olga Tkachova",
          "Xenia Khailenka",
          "Viacheslav Davidovich",
          "Andrey Narut",
          "Dzmitry Kazimirchyk",
        ]} className={itemClass}  />
      </div>
    </Grid>
    <Grid item xs={12} sm={6}>
      <CreditsItem heading="Quality assurance" creditPersons={[
        "George Filipovich",
        "Yury Harachka",
        "Aliaksei Haiduchonak",
        "Rex Chan"
      ]} className={itemClass}  />
      <CreditsItem heading="Icon design" creditPersons={["Bruce Martin"]} className={itemClass}  />
      <CreditsItem heading="Additional programming" creditPersons={[
        "Matthias Moeser",
        "Abdul Abdul-Latif",
        "Mosleh Uddin",
        "Savva Kolbachev",
        "Jackson Mills",
        "Ruslan Ibragimov",
        "Sasha Shestak"
      ]} className={itemClass}  />
      <CreditsItem heading="Documentation" creditPersons={[
        "James Swinbanks",
        "Flynn Hill",
        "Anne Marie Flores",
        "Charlotte Tanner",
        "Stephen McIlwaine",
      ]} className={itemClass}  />
    </Grid>
  </Grid>;
};

export default Credits;