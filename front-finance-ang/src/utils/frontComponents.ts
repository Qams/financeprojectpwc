import {MatSnackBar} from "@angular/material";

export const showSnackbar = (snackbar: MatSnackBar, msg: string) => {
  snackbar.open(msg, null, {
    duration: 3000,
  });
};
