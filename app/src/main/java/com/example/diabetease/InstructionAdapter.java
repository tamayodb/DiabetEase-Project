package com.example.diabetease;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;


import java.util.List;

public class InstructionAdapter extends RecyclerView.Adapter<InstructionAdapter.InstructionViewHolder> {

    private List<Instruction> instructionList;

    public InstructionAdapter(List<Instruction> instructionList) {
        this.instructionList = instructionList;
    }

    @NonNull
    @Override
    public InstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_instruction_card, parent, false);
        return new InstructionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionViewHolder holder, int position) {
        Instruction instruction = instructionList.get(position);
        holder.stepNumber.setText(String.valueOf(instruction.getStepNumber()));
        holder.stepText.setText(instruction.getText());
    }

    @Override
    public int getItemCount() {
        return instructionList.size();
    }

    static class InstructionViewHolder extends RecyclerView.ViewHolder {
        TextView stepNumber, stepText;

        public InstructionViewHolder(@NonNull View itemView) {
            super(itemView);
            stepNumber = itemView.findViewById(R.id.step_number);
            stepText = itemView.findViewById(R.id.step_text);
        }
    }
}

